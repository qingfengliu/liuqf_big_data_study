package org.TestJoin;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.json.JSONObject;

import java.util.Iterator;

//使用interval join并且定义side output输出迟到数据
public class UsingIntervalJoin {
    public static void main(String[] args) {
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

        DataStream<Tuple2<String, String>> sale_data = lines.map(new MapFunction<String, Tuple2<String, String>>() {
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
        );;

        //person data source
        KafkaSource<String> KfkPersonDataSource2 = KafkaSource.<String>builder()
                .setBootstrapServers("hadoop1:9092,hadoop2:9092,hadoop3:9092")
                .setTopics("person_random_data")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> lines2 = env.fromSource(KfkPersonDataSource2, WatermarkStrategy.noWatermarks(), "kafka source2");

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

        SingleOutputStreamOperator<String> hebing = sale_data.keyBy(new KeySelector<Tuple2<String, String>, String>() {
                    @Override
                    public String getKey(Tuple2<String, String> value) throws Exception {
                        return value.f0;
                    }
                })
                .intervalJoin(person_data.keyBy(new KeySelector<Tuple2<String, String>, String>() {
                    @Override
                    public String getKey(Tuple2<String, String> value) throws Exception {
                        return value.f0;
                    }
                }))
                .between(Time.seconds(-5), Time.seconds(5))
                .sideOutputLeftLateData(leftLateTag)
                .sideOutputRightLateData(rightLateTag)
                .process(new MyIntervalJoinFunction());
        //旁路输出,只有流水线移动后发送历史数据才生效。
        //假如在流水线移动之前,某个流只有一条数据到来,后续流水线移动(触发join流水线才会移动)
        //那么这条数据旁路输出也会丢失,
        //怎样将这条丢失的数据输出呢
        //下次尝试使用coGroup+connect
        hebing.getSideOutput(leftLateTag).print("left-late=");
        hebing.getSideOutput(rightLateTag).print("right-late=");
        hebing.map(new MapFunction<String, String>() {
            @Override
            public String map(String value) throws Exception {
                return value;
            }
        }).print();

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static final OutputTag<Tuple2<String, String>> leftLateTag = new OutputTag<Tuple2<String, String>>("left-output"){};
    static final OutputTag<Tuple2<String, String>> rightLateTag = new OutputTag<Tuple2<String, String>>("right-output"){};

    public static class MyIntervalJoinFunction extends ProcessJoinFunction<Tuple2<String, String>, Tuple2<String, String>, String> {

        @Override
        public void processElement(Tuple2<String, String> first, Tuple2<String, String> second, Context ctx, Collector<String> out) throws Exception {
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
//            System.out.println(ctx.getLeftTimestamp());
//            System.out.println(ctx.getRightTimestamp());
            System.out.println(ctx.getTimestamp());
//            System.out.println(jsonObject1.toString());
            //迟到的数据输出到侧输出流
//            ctx.output(outputTag, "sideout-"+jsonObject1.toString());
            out.collect(jsonObject1.toString());
        }

    }
}
