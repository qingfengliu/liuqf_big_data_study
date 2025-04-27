package org.liuqf;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class MainWindowTag {
    public static void main(String[] args) throws Exception {

        //1. env-准备环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 获取tableEnv对象
        // 通过env 获取一个table 环境
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        tEnv.executeSql("CREATE TABLE sale_order ( \n" +
                "\tname String, \n" +
                "\taddress  String, \n" +
                "\trestaurant String, \n" +
                "\tfood      String, \n" +
                "\t`count`      BIGINT, \n" +
                "\tprice      DOUBLE, \n" +
                "\tgmv      DOUBLE, \n" +
                "\t`tm` BIGINT,\n" +
                "\tts AS TO_TIMESTAMP_LTZ(tm, 3),\n" +
                "\tWATERMARK FOR ts AS ts - INTERVAL '5' SECOND\n" +
                ") WITH (\n" +
                "\t'connector'= 'kafka', \n" +
                "\t'topic'= 'sale_random_data',\n" +
                "\t'properties.bootstrap.servers'='hadoop1:9092,hadoop2:9092,hadoop3:9092',\n" +
                "\t'properties.group.id'='abc',\n" +
                "\t'scan.startup.mode' = 'latest-offset',\n" +
                "\t'format'= 'json' \n" +
                ");");
//        tEnv.executeSql("SELECT name,sum(gmv) gmv FROM TABLE(\n" +
//                "\tTUMBLE(TABLE sale_order, DESCRIPTOR(ts), INTERVAL '1' MINUTES)\n" +
//                ")\n" +
//                "group by name\n" +
//                ";").print();   //这个打印出现在运行程序的窗口
        Table resultTable = tEnv.sqlQuery("SELECT window_start,window_end,sum(gmv) gmv FROM TABLE(\n" +
                "\tTUMBLE(TABLE sale_order, DESCRIPTOR(ts), INTERVAL '1' MINUTES)\n" +
                ")\n" +
                "group by window_start,window_end;");
        DataStream<Row> resultStream = tEnv.toChangelogStream(resultTable);
//        Table resultTable = tEnv.sqlQuery("select * from sale_order");
//
//        // interpret the insert-only Table as a DataStream again
//        DataStream<Row> resultStream = tEnv.toDataStream(resultTable);


        // add a printing sink and execute in DataStream API
        resultStream.print();//这个在webui上可以看到
        env.execute();

    }
}
