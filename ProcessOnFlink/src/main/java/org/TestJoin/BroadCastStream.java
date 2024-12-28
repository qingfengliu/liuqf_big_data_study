package org.TestJoin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.json.JSONObject;
import org.apache.flink.connector.jdbc.JdbcInputFormat;
import java.util.Iterator;


public class BroadCastStream {
    public static void main(String[] args) {
        // Create a Flink execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //sale data source
        KafkaSource<String> KfkSaleDataSource = KafkaSource.<String>builder()
                .setBootstrapServers("hadoop1:9092,hadoop2:9092,hadoop3:9092")
                .setTopics("sale_random_data")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> lines = env.fromSource(KfkSaleDataSource, WatermarkStrategy.noWatermarks(), "kafka source");

        DataStream<Tuple2<String, String>> sale_data = lines.map(new MapFunction<String, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> map(String value) {
                JSONObject jsonObject = new JSONObject(value);
                return new Tuple2<String, String>(jsonObject.getString("name"), jsonObject.toString());

            }
        });

        //从mysql中取person数据
        DataStreamSource<Row> dataInput = env.createInput(
                //使用新的包flink-connector-jdbc读取mysql数据
                JdbcInputFormat.buildJdbcInputFormat()
                        .setDrivername("com.mysql.cj.jdbc.Driver")
                        .setDBUrl("jdbc:mysql://hadoop1:3306/random_data")
                        .setUsername("root")
                        .setPassword("111111")
                        .setQuery("")
                        .setRowTypeInfo(new RowTypeInfo(
                                BasicTypeInfo.STRING_TYPE_INFO,
                                BasicTypeInfo.STRING_TYPE_INFO,
                                BasicTypeInfo.STRING_TYPE_INFO,
                                BasicTypeInfo.STRING_TYPE_INFO,
                                BasicTypeInfo.STRING_TYPE_INFO,
                                BasicTypeInfo.STRING_TYPE_INFO
                        )).finish()
        );

        DataStream<Tuple2<String,Student>> person_data = dataInput.map(new MapFunction<Row, Student>() {
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
        }).map(new MapFunction<Student, Tuple2<String, Student>>() {
            @Override
            public Tuple2<String, Student> map(Student student) throws Exception {
                return new Tuple2<>(student.getName(), student);
            }
        });

        final MapStateDescriptor<String, Student> broadcastDesc = new MapStateDescriptor<>("broad1", String.class, Student.class);
        BroadcastStream<Tuple2<String, Student>> broadcastStream = person_data.broadcast(broadcastDesc);

        DataStream<Tuple3<String, String, Student>> result = sale_data.connect(broadcastStream)
                .process(new BroadcastProcessFunction<Tuple2<String, String>, Tuple2<String, Student>, Tuple3<String, String, Student>>() {
                    //处理非广播流，关联维度
                    @Override
                    public void processElement(Tuple2<String, String> value, ReadOnlyContext ctx, Collector<Tuple3<String, String, Student>> out) throws Exception {
                        ReadOnlyBroadcastState<String, Student> state = ctx.getBroadcastState(broadcastDesc);
                        Student name = null;
                        if (state.contains(value.f0)) {
                            name = (Student) state.get(value.f0);

                        }
                        out.collect(new Tuple3<>(value.f0, value.f1, name));
                    }

                    @Override
                    public void processBroadcastElement(Tuple2<String, Student> value, Context ctx, Collector<Tuple3<String, String, Student>> out) throws Exception {
                        //System.out.println("收到广播数据：" + value);
                        ctx.getBroadcastState(broadcastDesc).put(value.f0, value.f1);
                    }
                });
        result.map(
                new MapFunction<Tuple3<String, String, Student>, String>() {
                    @Override
                    public String map(Tuple3<String, String, Student> value) {
                        JSONObject jsonObject1 = new JSONObject(value.f1);
                        JSONObject jsonObject2 = new JSONObject(value.f2);
                        Iterator<String> keys = jsonObject2.keys();
                        while (keys.hasNext()) {
                            String key_1 = keys.next();
                            if (jsonObject1.has(key_1) && !key_1.equals("name")) {
                                jsonObject1.put(key_1 + "_A", jsonObject1.get(key_1));
                                jsonObject1.put(key_1 + "_B", jsonObject2.get(key_1));
                                jsonObject1.remove(key_1);
                            } else {
                                jsonObject1.put(key_1, jsonObject2.get(key_1));
                            }
                        }
                        return jsonObject1.toString();
                    }
                }
        ).print();

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
