package org.liuqf;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.commons.io.output.NullPrintStream;
import org.apache.flink.types.Row;

public class Main {
    public static void main(String[] args) throws Exception {

        //1. env-准备环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 获取tableEnv对象
        // 通过env 获取一个table 环境
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        tEnv.executeSql("CREATE TABLE sale_order ( \n" +
                "\tname  String, \n" +
                "\taddress      String, \n" +
                "\trestaurant      String, \n" +
                "\tfood      String, \n" +
                "\t`count`      BIGINT, \n" +
                "\tprice      DOUBLE, \n" +
                "\tgmv      DOUBLE, \n" +
                "\t`tm` TIMESTAMP_LTZ(3) METADATA FROM 'timestamp'\n" +
                ") WITH (\n" +
                "\t'connector'= 'kafka', \n" +
                "\t'topic'= 'sale_random_data',\n" +
                "\t'properties.bootstrap.servers'='hadoop1:9092,hadoop2:9092,hadoop3:9092',\n" +
                "\t'properties.client.id'='flink-connector-kafka-0',\n" +
                "\t'properties.group.id'='abc',\n" +
                "\t'scan.startup.mode' = 'latest-offset',\n" +
                "\t'format'= 'json' \n" +
                ");");
//        tEnv.executeSql("select * from sale_order;").print();
        Table resultTable = tEnv.sqlQuery("select * from sale_order");

        // interpret the insert-only Table as a DataStream again
        DataStream<Row> resultStream = tEnv.toDataStream(resultTable);

        // add a printing sink and execute in DataStream API
        resultStream.print();
        env.execute();

        //上边这种方式在1.20中使用webui提交的时候会报错。flink sql Job client must be a CoordinationRequestGateway.
        //
        //用下边命令可以运行
        // /opt/flink/bin/flink run -m yarn-cluster -c org.liuqf.Main GoToRestaurant-1.0-SNAPSHOT.jar
        //5. execute-执行
//        env.execute();
    }
}