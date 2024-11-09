package org.TestSink;

import lombok.Getter;
import lombok.Setter;
import org.apache.doris.flink.cfg.DorisExecutionOptions;
import org.apache.doris.flink.cfg.DorisOptions;
import org.apache.doris.flink.cfg.DorisReadOptions;
import org.apache.doris.flink.sink.DorisSink;
import org.apache.doris.flink.sink.writer.serializer.RowDataSerializer;
import org.apache.flink.api.common.RuntimeExecutionMode;
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
import org.apache.flink.orc.vector.Vectorizer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.types.DataType;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.hadoop.hive.ql.exec.vector.BytesColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.DoubleColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.LongColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class UsingCoGroupJoinToDoris {
    @Getter
    @Setter
    public static class Sale{
        String name;
        String address;
        String restaurant;
        String food;
        int count;
        double price;
        double gmv;
        long tm;

        @Override
        public String toString() {
            return "{" +
                    "name:'" + name + '\'' +
                    ", address:'" + address + '\'' +
                    ", restaurant:'" + restaurant + '\'' +
                    ", food:'" + food + '\'' +
                    ", price:" + price +
                    ", count:" + count +
                    ", gmv:" + gmv +
                    ", tm:" + tm +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class Person{
        String name;
        String address;
        String phone;
        String email;
        String job;
        String company;
        long tm;

        @Override
        public String toString() {
            return "{" +
                    "name:'" + name + '\'' +
                    ", address:'" + address + '\'' +
                    ", phone:'" + phone + '\'' +
                    ", email:'" + email + '\'' +
                    ", job:'" + job + '\'' +
                    ", company:'" + company + '\'' +
                    ", tm:" + tm +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class SalePerson{
        String name;
        String address;
        String restaurant;
        String food;
        double price;
        int count;
        double gmv;
        String email;
        String phone;
        String job;
        String company;

        long tm;
        String dt;

        //静态内部类可以new
        @Override
        public String toString() {
            return "{" +
                    "name:'" + name + '\'' +
                    ", address:'" + address + '\'' +
                    ", restaurant:'" + restaurant + '\'' +
                    ", food:'" + food + '\'' +
                    ", price:" + price +
                    ", count:" + count +
                    ", gmv:" + gmv +
                    ", email:'" + email + '\'' +
                    ", phone:'" + phone + '\'' +
                    ", job:'" + job + '\'' +
                    ", company:'" + company + '\'' +
                    ", tm:" + tm +
                    ", dt:'" + dt + '\'' +
                    '}';
        }
    }



    //初始化两个数据源，将数据装载到两个结构体中。并设置时间戳和水印
    public static Tuple2<DataStream, DataStream> setup(StreamExecutionEnvironment env){
        //sale data source

        KafkaSource<String> KfkSaleDataSource = KafkaSource.<String>builder()
                .setBootstrapServers("hadoop1:9092,hadoop2:9092,hadoop3:9092")
                .setTopics("sale_random_data")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> lines = env.fromSource(KfkSaleDataSource, WatermarkStrategy.noWatermarks(), "kafka source");

        //sale data source
        DataStream<Sale> sale_data = lines.map(new MapFunction<String, Sale>() {
            @Override
            public Sale map(String value) {

                JSONObject jsonObject = new JSONObject(value);
                Sale sale_data = new Sale();
                sale_data.setName(jsonObject.getString("name"));
                sale_data.setAddress(jsonObject.getString("address"));
                sale_data.setRestaurant(jsonObject.getString("restaurant"));
                sale_data.setFood(jsonObject.getString("food"));
                sale_data.setPrice(jsonObject.getDouble("price"));
                sale_data.setCount(jsonObject.getInt("count"));
                sale_data.setGmv(jsonObject.getDouble("gmv"));
                sale_data.setTm(jsonObject.getLong("tm"));
                return sale_data;
            }
        }).assignTimestampsAndWatermarks(
                WatermarkStrategy.<Sale>forMonotonousTimestamps()
                        .withTimestampAssigner(
                                (event, timestamp) -> event.getTm()
                        )
        );

        //person data source
        KafkaSource<String> KfkPersonDataSource2 = KafkaSource.<String>builder()
                .setBootstrapServers("hadoop1:9092,hadoop2:9092,hadoop3:9092")
                .setTopics("person_random_data")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> lines2 = env.fromSource(KfkPersonDataSource2, WatermarkStrategy.noWatermarks(), "kafka source2");

        DataStream<Person> person_data = lines2.map(new MapFunction<String, Person>() {
            @Override
            public Person map(String value) {
                JSONObject jsonObject = new JSONObject(value);
                Person person_data = new Person();
                person_data.setName(jsonObject.getString("name"));
                person_data.setAddress(jsonObject.getString("address"));
                person_data.setPhone(jsonObject.getString("phone"));
                person_data.setEmail(jsonObject.getString("email"));
                person_data.setJob(jsonObject.getString("job"));
                person_data.setCompany(jsonObject.getString("company"));
                person_data.setTm(jsonObject.getLong("tm"));
                return person_data;
            }
        }).assignTimestampsAndWatermarks(
                WatermarkStrategy.<Person>forMonotonousTimestamps()
                        .withTimestampAssigner((event, timestamp) -> event.getTm()
                        )
        );
        return new Tuple2<DataStream,DataStream>(sale_data,person_data);
    }

    public static class myCoProcessFunction extends CoProcessFunction<Sale, Person, SalePerson> {

        private static final long WAIT_TIME = 5000L;

        // 某个key在processElement1中存入的状态
        private ValueState<Sale> state1;

        // 某个key在processElement2中存入的状态
        private ValueState<Person> state2;

        // 如果创建了定时器，就在状态中保存定时器的key
        private ValueState<Long> timerState;

        // onTimer中拿不到当前key，只能提前保存在状态中（KeyedProcessFunction的OnTimerContext有API可以取到，但是CoProcessFunction的OnTimerContext却没有）
        private ValueState<String> currentKeyState;

        //两个侧输出流
        private final OutputTag<String> source1SideOutput = new OutputTag<String>("left-output"){};
        private final OutputTag<String> source2SideOutput = new OutputTag<String>("right-output"){};



        @Override
        public void open(Configuration parameters) throws Exception {
            // 初始化状态
            state1 = getRuntimeContext().getState(new ValueStateDescriptor<>("myState1", Sale.class));
            state2 = getRuntimeContext().getState(new ValueStateDescriptor<>("myState2", Person.class));
            timerState = getRuntimeContext().getState(new ValueStateDescriptor<>("timerState", Long.class));
            currentKeyState = getRuntimeContext().getState(new ValueStateDescriptor<>("currentKeyState", String.class));
        }

        /**
         * 所有状态都清理掉
         */
        private void clearAllState() {
            state1.clear();
            state2.clear();
            currentKeyState.clear();
            timerState.clear();
        }

        public SalePerson chuli(Sale first, Person second) throws Exception {
            //将两个数据源的数据合并。如果某个变量a b均有以a为准
            //暂时放在这里,后续放到数据生成器中加入dt
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime dateTime = LocalDateTime.now();
            String formattedDateTime = dateTime.format(formatter);

            SalePerson salePerson = new SalePerson();
            salePerson.setName(first.getName());
            salePerson.setRestaurant(first.getRestaurant());
            salePerson.setAddress(first.getAddress());
            salePerson.setFood(first.getFood());
            salePerson.setPrice(first.getPrice());
            salePerson.setCount(first.getCount());
            salePerson.setGmv(first.getGmv());
            salePerson.setEmail(second.getEmail());
            salePerson.setPhone(second.getPhone());
            salePerson.setJob(second.getJob());
            salePerson.setCompany(second.getCompany());
            salePerson.setTm(first.getTm());
            salePerson.setDt(formattedDateTime);
            return salePerson;
        }

        @Override
        public void processElement1(Sale value, Context ctx, Collector<SalePerson> out) throws Exception {

            String key = value.getName();
            Person value2 = state2.value();
            if (value2 != null) {
                //2号流收到过数据那么执行join流程,并且删除定时器。删除定时器的时候需要那个传入的超时时间
                System.out.println("process left in and process right not null");
                System.out.println("process left in and process right not null currentWatermark:"+ctx.timerService().currentWatermark());
                out.collect(chuli(value, value2));
                long timerKey = timerState.value();
                ctx.timerService().deleteEventTimeTimer(timerKey);
                //所有状态清空
                clearAllState();
            }else{
                System.out.println("process left in and process right null");
                //2号流没有收到数据,那么建立定时器。建立定时器传入超时时间。
                state1.update(value);

                currentKeyState.update(key);
                long timerKey = ctx.timestamp()+WAIT_TIME;

                System.out.println("process left in and process right null currentWatermark:"+ctx.timerService().currentWatermark());
                System.out.println("process left in and process right null timerKey:"+timerKey);
//                System.out.println("registerEventTimeTimer");
                ctx.timerService().registerEventTimeTimer(timerKey);
                //保存定时器的key
                timerState.update(timerKey);

            }
        }

        @Override
        public void processElement2(Person value, Context ctx, Collector<SalePerson> out) throws Exception {
            String key = value.getName();
            Sale value1 = state1.value();
            if (value1 != null) {
                System.out.println("process right in and process left not null");
                System.out.println("process left in and process left not null currentWatermark:"+ctx.timerService().currentWatermark());
                //1号流收到过数据，触发join，并且删除定时器
                out.collect(chuli(value1,value));

                long timerKey = timerState.value();
                ctx.timerService().deleteEventTimeTimer(timerKey);

                clearAllState();
            }else{
                System.out.println("process right in and process left null");
                //1号流如果没收到数据,注册定时器，注册定时器的时候需要一个超时时间
                state2.update(value);
                currentKeyState.update(key);

                // 开始10秒的定时器，10秒后会进入
                long timerKey = ctx.timestamp() + WAIT_TIME;
                System.out.println("process right in and process left null currentWatermark:"+ctx.timerService().currentWatermark());
                System.out.println("process right in and process left null timerKey:"+timerKey);

                ctx.timerService().registerEventTimeTimer(timerKey);
                // 保存定时器的key
                timerState.update(timerKey);
            }
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<SalePerson> out) throws Exception {
//            super.onTimer(timestamp, ctx, out);
            // 定时器触发，说明另一个流没有数据，所以清空所有状态

            Sale value1 = state1.value();
            Person value2 = state2.value();
            System.out.println("process on time on key:" + currentKeyState.value());
            if(null!=value1) {
                // 侧输出,暂时不给侧输出流添加sink
                ctx.output(source1SideOutput, value1.toString());
            }

            if(null!=value2) {
                // 侧输出,暂时不给侧输出流添加sink
                ctx.output(source2SideOutput, value2.toString());
            }

            clearAllState();
        }

    }

    
    public static void main(String[] args) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getCheckpointConfig().setCheckpointInterval(1000);

        Tuple2<DataStream, DataStream> to_stream = setup(env);
        DataStream<Sale> sale_data = to_stream.f0;
        DataStream<Person> person_data = to_stream.f1;
        SingleOutputStreamOperator<SalePerson> chuli=sale_data.keyBy(new KeySelector<Sale, String>() {
            @Override
            public String getKey(Sale value) throws Exception {
                return value.getName();
            }
        }).connect(person_data.keyBy(new KeySelector<Person, String>() {
            @Override
            public String getKey(Person value) throws Exception {
                return value.getName();
            }
        })).process(new myCoProcessFunction()).map(
                new MapFunction<SalePerson, SalePerson>() {
                    @Override
                    public SalePerson map(SalePerson value) {
                        return value;
                    }
                }
        );

        DorisSink.Builder<RowData> builder = DorisSink.builder();
        DorisOptions.Builder dorisBuilder = DorisOptions.builder();
        //这里没有密码怎样处理

        dorisBuilder.setFenodes("hadoop1:8035")
                .setTableIdentifier("random_data.xiaofei_kuanb2")
                .setUsername("root")
                .setPassword("");

        Properties properties = new Properties();
        properties.setProperty("format", "json");
        properties.setProperty("read_json_by_line", "true");
        DorisExecutionOptions.Builder  executionBuilder = DorisExecutionOptions.builder();
        executionBuilder.setLabelPrefix("label-doris") //streamload label prefix
                .setDeletable(false)
                .setStreamLoadProp(properties); //streamload params
        //dt是分区字段

        String[] fields = {"name", "address", "restaurant", "food", "price", "count", "gmv", "email", "phone", "job", "company", "tm", "dt"};
        DataType[] types = {DataTypes.VARCHAR(200), DataTypes.VARCHAR(500), DataTypes.VARCHAR(500),
                DataTypes.VARCHAR(500), DataTypes.DOUBLE(), DataTypes.INT(), DataTypes.DOUBLE(), DataTypes.STRING(),
                DataTypes.STRING(), DataTypes.STRING(), DataTypes.STRING(), DataTypes.BIGINT(), DataTypes.STRING()};

        builder.setDorisReadOptions(DorisReadOptions.builder().build())
                .setDorisExecutionOptions(executionBuilder.build())
                .setSerializer(RowDataSerializer.builder()    //serialize according to rowdata
                        .setFieldNames(fields)
                        .setType("json")           //json format
                        .setFieldType(types).build())
                .setDorisOptions(dorisBuilder.build());

        DataStream<RowData> source = chuli.map(new MapFunction<SalePerson, RowData>() {
            @Override
            public RowData map(SalePerson value) {
                GenericRowData genericRowData = new GenericRowData(13);
                genericRowData.setField(0, StringData.fromString(value.getName()));
                genericRowData.setField(1, StringData.fromString(value.getAddress()));
                genericRowData.setField(2, StringData.fromString(value.getRestaurant()));
                genericRowData.setField(3, StringData.fromString(value.getFood()));
                genericRowData.setField(4, value.getPrice());
                genericRowData.setField(5, value.getCount());
                genericRowData.setField(6, value.getGmv());
                genericRowData.setField(7, StringData.fromString(value.getEmail()));
                genericRowData.setField(8, StringData.fromString(value.getPhone()));
                genericRowData.setField(9, StringData.fromString(value.getJob()));
                genericRowData.setField(10, StringData.fromString(value.getCompany()));
                genericRowData.setField(11, value.getTm());
                //dt是分区字段,用字符串插入即可。
                genericRowData.setField(12, StringData.fromString(value.getDt()));


                return genericRowData;
            }
        });

//        chuli.print();
        source.sinkTo(builder.build());
        source.print();
        try {
            env.execute("Flink Streaming Java API Skeleton");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
