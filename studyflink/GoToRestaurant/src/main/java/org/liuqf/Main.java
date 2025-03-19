package org.liuqf;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

public class Main {
    public static void main(String[] args) {
        //flink sql
        EnvironmentSettings settings = EnvironmentSettings.inStreamingMode();
        TableEnvironment tEnv = TableEnvironment.create(settings);

        tEnv.executeSql("CREATE TABLE order (\n" +
                "    name  String,\n" +
                "    food      String,\n" +
                "    address      String,\n" +
                "    restaurant      String,\n" +
                "    count      BIGINT,\n" +
                "    price      DOUBLE,\n" +
                "    price      DOUBLE,\n" +
                "    tm TIMESTAMP(3),\n" +
                "    WATERMARK FOR transaction_time AS transaction_time - INTERVAL '5' SECOND\n" +
                ") WITH (\n" +
                "    'connector' = 'kafka',\n" +
                "    'topic'     = 'transactions',\n" +
                "    'properties.bootstrap.servers' = 'kafka:9092',\n" +
                "    'format'    = 'csv'\n" +
                ")");

        //试验未做完，数据生成器缺少时间戳
        //https://nightlies.apache.org/flink/flink-docs-release-1.20/zh/docs/try-flink/table_api/
        //https://www.cnblogs.com/mangod/p/18187474
    }
}