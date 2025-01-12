package org.example;

import DataStruct.StructData;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

//简单的测试一下valueState
public class TestState01 {
    //不能放到同一文件的其他类中，否则会报错

    //静态内部类
    public static void main(String[] args) {


        Configuration config = new Configuration();
        // 设置配置参数（尽管 heartbeat.timeout 在本地环境中可能不起作用）
        config.setString("heartbeat.timeout", "60000"); // 设置心跳超时为60秒
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(config);

        //需要先启动nc -lk 9999。否则报错
        DataStreamSource<String> sales_source = env.socketTextStream("localhost", 9999);
        //创建一个带时间戳的流。
        SingleOutputStreamOperator<String> sales = sales_source.<String>assignTimestampsAndWatermarks(
                WatermarkStrategy.<String>forMonotonousTimestamps()//有序流.如果是无序流，可以使用forBoundedOutOfOrderness
                        .withTimestampAssigner((s, timestamp) -> {
                            String[] parts = s.split(",");
                            return Long.parseLong(parts[parts.length - 1]);
                        })
        );

        //格式key,value,timestamp
         SingleOutputStreamOperator<StructData> load_data=sales.keyBy(s -> s.split(",")[0])
                .map(new MapFunction<String, StructData>() {
                    @Override
                    public StructData map(String s) throws Exception {
                        String[] parts = s.split(",");
                        StructData data = new StructData();
                        data.key = parts[0];
                        data.count = Integer.parseInt(parts[1]);
                        data.last_modified = Long.parseLong(parts[2]);
                        return data;
                    }
                });
         load_data.keyBy(s->s.key)
                 //输入，状态，输出
                 .process(new KeyedProcessFunction<String,StructData,StructData>(){
                     //定义一个状态
                     private ValueState<StructData> averageState;

                     @Override
                     public void processElement(StructData value, KeyedProcessFunction.Context ctx, Collector out) throws Exception {
                            StructData current =  value;
                            StructData lastState = averageState.value();
                            if (lastState == null) {
                                lastState = new StructData();
                                lastState.key = current.key;
                                lastState.count = 0;
                                lastState.last_modified = 0;
                            }
                            lastState.count += current.count;
                            lastState.last_modified = current.last_modified;
                            averageState.update(lastState);
                            out.collect(lastState);
                     }

                    @Override
                    public void open(Configuration parameters) {
                         //定义状态的过期时间,这个好像是处理时间而不是事件时间。
                        StateTtlConfig stateTtlConfig = StateTtlConfig
                                .newBuilder(Time.seconds(10))  //过期时间10s
                                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)  //状态 创建和更新  更新过期时间
                                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)  //不返回过期的状态值
                                .build();

                        ValueStateDescriptor<StructData> myState1Descriptor = new ValueStateDescriptor<>("myState1", StructData.class);
                        myState1Descriptor.enableTimeToLive(stateTtlConfig);
                        averageState = getRuntimeContext().getState(myState1Descriptor);
                    }
                 }).map(s->s).print();

        try {
            env.execute("Test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


