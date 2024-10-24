package org.TestJoin;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.util.Collector;
import org.json.JSONObject;

import java.util.Iterator;

public class UsingCoGroupJoin {
    public static Tuple2<DataStream, DataStream> setup(StreamExecutionEnvironment env){

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

        DataStream<Tuple2<String, String>> person_data = lines2.map(new MapFunction<String, Tuple2<String, String>>() {
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
        return new Tuple2<DataStream,DataStream>(sale_data,person_data);
    }

    static CoProcessFunction<Tuple2<String, String>, Tuple2<String, String>, String> myCoProcessFunction = new CoProcessFunction<Tuple2<String, String>, Tuple2<String, String>, String>() {

        // 某个key在processElement1中存入的状态
        private ValueState<String> state1;

        // 某个key在processElement2中存入的状态
        private ValueState<String> state2;
        @Override
        public void open(Configuration parameters) throws Exception {
            // 初始化状态
            state1 = getRuntimeContext().getState(new ValueStateDescriptor<>("myState1", String.class));
            state2 = getRuntimeContext().getState(new ValueStateDescriptor<>("myState2", String.class));
        }

        @Override
        public void processElement1(Tuple2<String, String> value, Context ctx, Collector<String> out) throws Exception {

//            String key = value.f0;
            String value2 = state2.value();
            System.out.println("value1处理函数");
            if (value2 != null) {
                //2号流收到过[{}]，值是[{}];
                out.collect(chuli(value.f1, value2));
                // 把2号流的状态清理掉
                state2.clear();
            }else{
                //2号流还未收到过[{}]，把1号流收到的值[{}]保存起来

                state1.update(value.f1);
            }
        }

        @Override
        public void processElement2(Tuple2<String, String> value, Context ctx, Collector<String> out) throws Exception {
            //            String key = value.f0;
            String value1 = state1.value();
            System.out.println("value2处理函数");
            if (value1 != null) {
                //1号流收到过[{}]，值是[{}];
                out.collect(chuli(value.f1, value1));
                // 把2号流的状态清理掉
                state1.clear();
            }else{
                //2号流还未收到过[{}]，把1号流收到的值[{}]保存起来
                state2.update(value.f1);
            }
        }

        public String chuli(String first, String second) throws Exception {
            //创建一个新的json对象。把两个json按照key,value展开放到新的json对象中
            //如果两个jsonkey相同则前边的json的key的名字加收_A后边的json的key+_B
            JSONObject jsonObject1 = new JSONObject(first);
            JSONObject jsonObject2 = new JSONObject(second);

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
    };

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Tuple2<DataStream, DataStream> to_stream = setup(env);
        DataStream<Tuple2<String, String>> sale_data = to_stream.f0;
        DataStream<Tuple2<String, String>> person_data = to_stream.f1;
//        CoProcessFunction
        //改使用connect和coProcess组合.cogroupfunction不提供侧输出
        sale_data.keyBy(new KeySelector<Tuple2<String, String>, String>() {
            @Override
            public String getKey(Tuple2<String, String> value) throws Exception {
                return value.f0;
            }
        }).connect(person_data.keyBy(new KeySelector<Tuple2<String, String>, String>() {
            @Override
            public String getKey(Tuple2<String, String> value) throws Exception {
                return value.f0;
            }
        })).process(myCoProcessFunction).map(
                new MapFunction<String, String>() {
                    @Override
                    public String map(String value) {
                        return value;
                    }
                }
        ).print();

        try {
            env.execute("Flink Streaming Java API Skeleton");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
// 参考文章特别感谢
// https://blog.csdn.net/boling_cavalry/article/details/109629119?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522E0D85776-F00B-4AF7-B647-D5426A85D6B5%2522%252C%2522scm%2522%253A%252220140713.130102334.pc%255Fblog.%2522%257D&request_id=E0D85776-F00B-4AF7-B647-D5426A85D6B5&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~blog~first_rank_ecpm_v1~rank_v31_ecpm-4-109629119-null-null.nonecase&utm_term=CoProcessFunction&spm=1018.2226.3001.4450