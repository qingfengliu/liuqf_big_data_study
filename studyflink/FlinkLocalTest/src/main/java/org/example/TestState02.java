package org.example;


import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.KeyedStateStore;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

// 测试，mapstatus
public class TestState02 {
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


        sales.keyBy(s -> s.split(",")[0]).process(new KeyedProcessFunction<String, String, String>() {
            //定义一个map状态
            private MapState<String, String> mapState;

            public void open(Configuration parameters) {
                //初始化状态
                mapState = getRuntimeContext().getMapState(new MapStateDescriptor<String, String>("mapState", String.class, String.class));
            }
            @Override
            public void processElement(String s, Context context, Collector<String> collector) throws Exception {
                //将第二个逗号的字段作为mapsate的key，第三个逗号的字段作为value
                //value用于累加
                String[] parts = s.split(",");
                String key = parts[1];
                String value = parts[2];
                if (mapState.contains(key)) {
                    String oldValue = mapState.get(key);
                    mapState.put(key, String.valueOf(Integer.parseInt(oldValue) + Integer.parseInt(value)));
                } else {
                    mapState.put(key, value);
                }

                //输出本次key的mapstate的内容
                collector.collect(key + ":" + mapState.get(key));
            }
        }).print();

        try {
            env.execute("Test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
