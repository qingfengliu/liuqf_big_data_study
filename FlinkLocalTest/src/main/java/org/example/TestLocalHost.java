package org.example;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class TestLocalHost {

    //dataframe builder
    static DataStreamSource setup_from_socker(StreamExecutionEnvironment env, int port) {
        DataStreamSource<String> lines = env.socketTextStream("localhost", 9999);
        return lines;
    }

    public static void main(String[] args) {

        Configuration conf = new Configuration();
        conf.setInteger("rest.port", 8081);
        conf.setString("rest.address", "localhost");

        //create a StreamExecutionEnvironment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        //create webui,需要依赖flink-runtime-web。但是有时候即使maven配置了也不出现在idea的lib中。需要手动加一下
        //idea->project structure->modules->lib->+->from maven用maven repository的buildr可以搜索到
        //启动一个时候web仅有job,如果停止了job，webui也会停止
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        //socket data source
        //需要先启动nc -lk 9999。否则报错
        DataStreamSource sales = setup_from_socker(env, 9999);
        DataStreamSource person = setup_from_socker(env, 9998);


        DataStreamSource<String> lines = env.socketTextStream("localhost", 9999);
        lines.print();
        try {
            env.execute("Test");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}