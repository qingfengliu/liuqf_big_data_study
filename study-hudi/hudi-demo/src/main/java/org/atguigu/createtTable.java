package org.atguigu;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

public class createtTable {
    public static void main(String[] args) {
        // 获得sparkcontext
        SparkConf conf = new SparkConf()
                .setAppName("test-hudi")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.sql.catalog.spark_catalog","org.apache.spark.sql.hudi.catalog.HoodieCatalog")
                .set("spark.sql.extensions","org.apache.spark.sql.hudi.HoodieSparkSessionExtension");
        SparkSession spark = SparkSession.builder()
                .config(conf)
                .enableHiveSupport()
                .getOrCreate();

        spark.sql("create table default.hudi_cow_nonpcf_tbl (\n" +
                "  uuid int,\n" +
                "  name string,\n" +
                "  price double\n" +
                ") using hudi;");
        spark.close();
    }
}
