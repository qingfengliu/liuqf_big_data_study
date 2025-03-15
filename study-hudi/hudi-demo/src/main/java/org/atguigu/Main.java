package org.atguigu;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        // 获得sparkcontext
        SparkConf conf = new SparkConf()
                .setAppName("test-hudi")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        SparkSession spark = SparkSession.builder()
                .config(conf)
                .enableHiveSupport()
                .getOrCreate();

        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());

        // 创建一个包含字符串的RDD
        JavaRDD<String> rdd = sc.parallelize(Arrays.asList(
                "Alice,30",
                "Bob,25",
                "Charlie,35"
        ));

        // 定义DataFrame的schema
        StructType schema = DataTypes.createStructType(Arrays.asList(
                DataTypes.createStructField("name", DataTypes.StringType, false),
                DataTypes.createStructField("age", DataTypes.IntegerType, false)
        ));

        // 将RDD转换为Row对象的RDD，并应用schema
        JavaRDD<Row> rowRDD = rdd.map(Main::convertToRow);

        // 从Row对象的RDD创建DataFrame
        Dataset<Row> df = spark.createDataFrame(rowRDD, schema);

        df.show();
    }

    private static Row convertToRow(String record) {
        String[] fields = record.split(",");
        return RowFactory.create(fields[0], Integer.parseInt(fields[1]));
    }
}