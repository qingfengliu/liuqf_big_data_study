package org.liuqf;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class MainGroupNoWindow {
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
        Table resultTable = tEnv.sqlQuery("select name,sum(gmv) from sale_order group by name");
        //启动flink sql client
        // /opt/flink/bin/sql-client.sh
        // interpret the insert-only Table as a DataStream again
        DataStream<Row> resultStream = tEnv.toChangelogStream(resultTable);

        resultStream.print();
        env.execute();

        //上边这种方式在1.20中使用webui提交的时候会报错。可能是kafka依赖的 ，还是没找到原因

        //用下边命令可以运行
        // /opt/flink/bin/flink run -m yarn-cluster -c org.liuqf.Main GoToRestaurant-1.0-SNAPSHOT.jar
    }
}
