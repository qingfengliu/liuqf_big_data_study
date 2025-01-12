package org.example;
import LowLevelFunction.MyKeyedProcessFunction;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class TestLocalHost {



    public static void main(String[] args) {
        Configuration config = new Configuration();
        // 设置配置参数（尽管 heartbeat.timeout 在本地环境中可能不起作用）
        config.setString("heartbeat.timeout", "60000"); // 设置心跳超时为60秒
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(config);

        //create webui,需要依赖flink-runtime-web。但是有时候即使maven配置了也不出现在idea的lib中。需要手动加一下
        //idea->project structure->modules->lib->+->from maven用maven repository的buildr可以搜索到
        //启动一个时候web仅有job,如果停止了job，webui也会停止
        //Configuration conf = new Configuration();
//        conf.setInteger("rest.port", 8081);
//        conf.setString("rest.address", "localhost");
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        //socket data source
        //需要先启动nc -lk 9999。否则报错
        DataStreamSource<String> sales_source = env.socketTextStream("localhost", 9999);
        //创建一个带时间戳的流
        SingleOutputStreamOperator<String> sales = sales_source.<String>assignTimestampsAndWatermarks(
                WatermarkStrategy.<String>forMonotonousTimestamps()//有序流.如果是无序流，可以使用forBoundedOutOfOrderness
                        .withTimestampAssigner((s, timestamp) -> {
                            String[] parts = s.split(",");
                            System.out.println(parts[parts.length - 1]);
                            return Long.parseLong(parts[parts.length - 1]);
                        })
        );


        sales.keyBy(s -> s.split(",")[0])
                .process(new MyKeyedProcessFunction())
                .print();


        try {
            env.execute("Test");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}