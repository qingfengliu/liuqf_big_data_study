package org.example;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;

import java.util.Collections;
import java.util.Iterator;
import java.util.Arrays;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.functions.FlatMapIterator;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.json.JSONObject;
import org.apache.flink.api.java.functions.KeySelector;


public class Test {
    public static void main(String[] args) {
        // Create a Flink execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //sale data source
        KafkaSource<String> KfkSaleDataSource = KafkaSource.<String>builder()
                .setBootstrapServers("hadoop1:9092,hadoop2:9092,hadoop3:9092")
                .setTopics("sale_random_data")
//                .setStartingOffsets(OffsetsInitializer.earliest())
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        DataStreamSource<String> lines = env.fromSource(KfkSaleDataSource, WatermarkStrategy.noWatermarks(), "kafka source");

        //person data source
        KafkaSource<String> KfkPersonDataSource2 = KafkaSource.<String>builder()
                .setBootstrapServers("hadoop1:9092,hadoop2:9092,hadoop3:9092")
                .setTopics("person_random_data")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> lines2 = env.fromSource(KfkPersonDataSource2, WatermarkStrategy.noWatermarks(), "kafka source2");


        SingleOutputStreamOperator<Tuple2<String, String>> sale_data = lines.map(new MapFunction<String, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> map(String value) {
                JSONObject jsonObject = new JSONObject(value);
                return new Tuple2<String, String>(jsonObject.getString("name"), jsonObject.toString());

            }
        }).assignTimestampsAndWatermarks(
                WatermarkStrategy.<Tuple2<String, String>>forMonotonousTimestamps()
                        .withTimestampAssigner((event, timestamp) -> {
                            JSONObject jsonObject = new JSONObject(event.f1);
                            return jsonObject.getLong("tm");
                        })
        );

        SingleOutputStreamOperator<Tuple2<String, String>> person_data = lines2.map(new MapFunction<String, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> map(String value) {
                JSONObject jsonObject = new JSONObject(value);
                return new Tuple2<String, String>(jsonObject.getString("name"), value);
            }
        }).assignTimestampsAndWatermarks(
                WatermarkStrategy.<Tuple2<String, String>>forMonotonousTimestamps()
                        .withTimestampAssigner((event, timestamp) -> {
                            JSONObject jsonObject = new JSONObject(event.f1);
                            return jsonObject.getLong("tm");
                        })
        );


        sale_data.join(person_data)
                .where(new KeySelector<Tuple2<String, String>, String>() {
                    @Override
                    public String getKey(Tuple2<String, String> value) {
                        return value.f0;
                    }
                })
                .equalTo(new KeySelector<Tuple2<String, String>, String>() {
                    @Override
                    public String getKey(Tuple2<String, String> value) {
                        return value.f0;
                    }
                })
                //滑动窗口,窗口有三种，滚动窗口，滑动窗口，会话窗口
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
//                .window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                .apply(new JoinFunction<Tuple2<String, String>, Tuple2<String, String>, String>() {
                    @Override
                    public String join(Tuple2<String, String> first, Tuple2<String, String> second) {
                        //创建一个新的json对象。把两个json按照key,value展开放到新的json对象中
                        //如果两个jsonkey相同则前边的json的key的名字加收_A后边的json的key+_B
                        JSONObject jsonObject1 = new JSONObject(first.f1);
                        JSONObject jsonObject2 = new JSONObject(second.f1);

                        Iterator<String> keys = jsonObject2.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            //字符串
                            if (jsonObject1.has(key)&& !key.equals("name")) {
                                jsonObject1.put(key + "_A", jsonObject1.get(key));
                                jsonObject1.put(key + "_B", jsonObject2.get(key));
                                //删除key
                                jsonObject1.remove(key);
                            } else {
                                jsonObject1.put(key, jsonObject2.get(key));
                            }
                        }
                        return jsonObject1.toString();
                    }
                }).print();


        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}