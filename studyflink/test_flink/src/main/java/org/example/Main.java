package org.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.FlatMapIterator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Collections;
import java.util.Iterator;
import java.util.Arrays;
import org.apache.flink.api.common.functions.MapFunction;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> kfkSource = KafkaSource.<String>builder()
                .setBootstrapServers("hadoop1:9092,hadoop2:9092,hadoop3:9092")
//                .setGroupId("flink")
                .setTopics("test")
//                .setStartingOffsets(OffsetsInitializer.earliest())
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        DataStreamSource<String> lines = env.fromSource(kfkSource, WatermarkStrategy.noWatermarks(), "kafka source");

        //根据逗号分组
        SingleOutputStreamOperator<Tuple2<String, Double>> map = lines.flatMap(new FlatMapIterator<String, Tuple2<String, Double>>() {
            @Override
            //将json字符串转换为name和price
            //返回一个迭代器,迭代器中包含name和price
            public Iterator<Tuple2<String, Double>> flatMap(String s) {
                JSONObject ob = new JSONObject(s);
                String name = ob.getString("name");
                Double price = ob.getDouble("gmv");
                return new Iterator<Tuple2<String, Double>>() {
                    boolean flag = true;

                    @Override
                    public boolean hasNext() {
                        return flag;
                    }

                    @Override
                    public Tuple2<String, Double> next() {
                        flag = false;
                        return new Tuple2<>(name, price);
                    }
                };
            }
        }).map(new MapFunction<Tuple2<String, Double>, Tuple2<String, Double>>() {
            @Override
            public Tuple2<String, Double> map(Tuple2<String, Double> s) {
                return new Tuple2<>(s.f0, s.f1);
            }
        });

        SingleOutputStreamOperator<Tuple2<String, Double>> sum = map.keyBy(0).sum(1);
        sum.print();
        
        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}