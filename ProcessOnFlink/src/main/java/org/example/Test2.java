package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.types.Row;
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
        }).assignTimestampsAndWatermarks(
                WatermarkStrategy.<Tuple2<String, String>>forMonotonousTimestamps()
                        .withTimestampAssigner((event, timestamp) -> {
                            JSONObject jsonObject = new JSONObject(event.f1);
                            return jsonObject.getLong("tm");
                        })
        );

        //person数据从mysql中读取
        DataStreamSource<Row> dataInput = env.createInput(JDBCInputFormat.buildJDBCInputFormat()
                .setDrivername("com.mysql.cj.jdbc.Driver")
                .setDBUrl("jdbc:mysql://hadoop1:3306/test")
                .setUsername("root")
                .setPassword("111111")
                .setQuery("SELECT name,address,phone,email,company,job from random_data.person_data")
                .setRowTypeInfo(new RowTypeInfo(  // 设置查询的列的类型
                        BasicTypeInfo.STRING_TYPE_INFO,  // name：String类型
                        BasicTypeInfo.STRING_TYPE_INFO,  // address：String类型
//                                BasicTypeInfo.INT_TYPE_INFO
                        BasicTypeInfo.STRING_TYPE_INFO, // phone：String类型
                        BasicTypeInfo.STRING_TYPE_INFO, // email：String类型
                        BasicTypeInfo.STRING_TYPE_INFO, // company：String类型
                        BasicTypeInfo.STRING_TYPE_INFO  // job：String类型
                )).finish());
        //将person数据转换为Student对象
        SingleOutputStreamOperator<Student> studentDataSet = dataInput.map(new MapFunction<Row, Student>() {
            @Override
            public Student map(Row row) throws Exception {
                return new Student(
                        row.getField(0).toString(),
                        row.getField(1).toString(),
                        row.getField(2).toString(),
                        row.getField(3).toString(),
                        row.getField(4).toString(),
                        row.getField(5).toString());
            }
        });

        sale_data.join(studentDataSet)
                .where(t -> t.f0)
                .equalTo(Student::getName)
                .window(EndOfStreamWindows.get())
                .apply(
                        new JoinFunction<Tuple2<String, String>, Student, Tuple2<String, String>>() {
                            @Override
                            public Tuple2<String, String> join(Tuple2<String, String> t1, Student t2) {
                                return new Tuple2<>(t1.f0, t2.toString());
                            }
                        }

                )
                .print();
        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Data  // 注解在类上，为类提供读写属性，还提供equals()、hashCode()、toString()方法
    @AllArgsConstructor  // 注解在类上，为类提供全参构造函数，参数的顺序与属性定义的顺序一致
    @NoArgsConstructor  // 注解在类上，为类提供无参构造函数
    public static class Student {
        private String name;
        private String address;
        private String phone;
        private String email;
        private String company;
        private String job;
    }
}
