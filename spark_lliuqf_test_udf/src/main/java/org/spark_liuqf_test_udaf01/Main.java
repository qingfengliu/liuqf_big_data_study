package org.spark_liuqf_test_udaf01;

import org.apache.spark.sql.SparkSession;

public class Main {
    public static void main(String[] args) {
        //将编写好的udaf函数注册到spark中
        SparkSession spark = SparkSession
                .builder()
                .master("spark://hadoop1:7077")
                .config("spark.executor.memory", "512m")
                .config("spark.executor.cores", "2")
                .appName("Java Spark SQL udf example")
                .enableHiveSupport()
                .getOrCreate();
        spark.udf().register("test_spark_udaf", new Test_spark_udaf());
        spark.sql("SELECT useid,test_spark_udaf(num) FROM test.test_udaf_avg GROUP BY  useid;").show();

        spark.stop();
    }
}
