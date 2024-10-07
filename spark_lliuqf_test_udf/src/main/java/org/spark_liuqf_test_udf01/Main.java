package org.spark_liuqf_test_udf01;


import org.apache.spark.sql.SparkSession;


public class Main  {
    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .master("spark://hadoop1:7077")
                .config("spark.executor.memory", "512m")
                .config("spark.executor.cores", "2")
                .config("spark.sql.warehouse.dir", "hdfs://hadoop1:9000/user/hive/warehouse")
                .config("hive.metastore.uris", "thrift://hadoop1:9083")
                .appName("Java Spark SQL udf example")
                .enableHiveSupport()
                .getOrCreate();
        spark.udf().register("pluspj", new Pluspj(), org.apache.spark.sql.types.DataTypes.StringType);
        spark.sql("SELECT listing_id,pluspj(available) FROM test.aby_albany_calendar where dt='2024-10-04';").show();
//        spark.sql("SELECT * FROM test.aby_albany_calendar where dt='2024-10-04';").show();
        spark.stop();
    }
    //下边用于测试集群有没有问题
//    public static void main(String[] args) {
//        SparkSession spark = SparkSession
//                .builder()
//                .master("spark://hadoop1:7077")
//                .config("spark.executor.memory", "512m")
//                .config("spark.executor.cores", "2")
//                .appName("Java Spark SQL basic example")
//                .enableHiveSupport()
//                .getOrCreate();
//
//        spark.sql("SELECT * FROM test.aby_albany_calendar where dt='2024-10-04';").show();
//        spark.stop();
//    }

}
