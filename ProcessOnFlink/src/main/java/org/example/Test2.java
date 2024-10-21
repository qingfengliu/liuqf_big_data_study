package org.example;


import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


import org.json.JSONObject;

import java.util.Iterator;
public class Test2 {
    public static void main(String[] args) {
        //试验批流join，批数据从mysql中读取，流数据从kafka中读取
        // Create a Flink execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //sale data source
        KafkaSource<String> KfkPersonDataSource2 = KafkaSource.<String>builder()
                .setBootstrapServers("hadoop1:9092,hadoop2:9092,hadoop3:9092")
                .setTopics("sale_random_data")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> lines=env.fromSource(KfkPersonDataSource2, WatermarkStrategy.noWatermarks(), "kafka source");
        //lines将json的name列做为key，value列做为value
        SingleOutputStreamOperator<Tuple2<String, String>> sale_data = lines.map(new MapFunction<String, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> map(String value) {
                JSONObject jsonObject = new JSONObject(value);
                return new Tuple2<String, String>(jsonObject.getString("name"), jsonObject.toString());

            }
        });
        sale_data.map(new StaticJoinDemo()).print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
