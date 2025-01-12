package org.TestProcess;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.json.JSONObject;

import java.util.Iterator;

public class UsingWinodowProcess {
    public DataStream<String> setup(StreamExecutionEnvironment env){
        //建立从kafaka读取数据并,指明事件时间字段
        KafkaSource<String> KfkSaleDataSource = KafkaSource.<String>builder()
                .setBootstrapServers("hadoop1:9092,hadoop2:9092,hadoop3:9092")
                .setTopics("sale_random_data")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> lines = env.fromSource(KfkSaleDataSource, WatermarkStrategy.noWatermarks(), "kafka source");

        DataStream<String> sale_data = lines.map(new MapFunction<String,String>() {
            @Override
            public String map(String value) {
                return value;
            }
        }).assignTimestampsAndWatermarks(
                WatermarkStrategy.<String>forMonotonousTimestamps()
                        .withTimestampAssigner((event, timestamp) -> {
                            JSONObject jsonObject = new JSONObject(event);
                            return jsonObject.getLong("tm");
                        })
        );
        return sale_data;
    }
    //IN, OUT, KEY, W extends Window
    public static class MyProcessWindowFunction extends ProcessWindowFunction<String, Tuple2<String,Integer>, String, TimeWindow> {
        private ValueState<Tuple2<String,Integer>> state;

        @Override
        public void process(String key, Context context, Iterable<String> elements, Collector<Tuple2<String,Integer>> out) throws Exception {
            int count = 0;
            System.out.println("in process key: " + key);
            Iterator<String> iterator = elements.iterator();
            while (iterator.hasNext()) {
                count++;
                //一定留着否则死循环了
                String next = iterator.next();
            }
            out.collect(new Tuple2<>(key,count));
        }
    }


    public static void main(String[] args) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        UsingWinodowProcess usingWinodowProcess = new UsingWinodowProcess();
        DataStream<String> sale_data = usingWinodowProcess.setup(env);

        OutputTag<String> source1SideOutput = new OutputTag<String>("late-data"){};

        SingleOutputStreamOperator<Tuple2<String,Integer>> mid = sale_data.keyBy((String value) -> {
                    JSONObject jsonObject = new JSONObject(value);
                    return jsonObject.getString("name");
                }).window(TumblingEventTimeWindows.of(Time.seconds(5)))  //滚动窗口
                .allowedLateness(Time.seconds(1))  //允许迟到1秒
                .sideOutputLateData(source1SideOutput)
                .process(new MyProcessWindowFunction());

        mid.map(new MapFunction<Tuple2<String,Integer>, String>() {
            @Override
            public String map(Tuple2<String,Integer> value) {
                return value.toString();
            }
        }).print();
        //Tuple2<String,Integer>
        DataStream<String> lateStream = mid.getSideOutput(source1SideOutput);
        lateStream.print("late data");
        try {
            env.execute("UsingWinodowProcess");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
