package org.spark_liuqf_test_udf01;

import org.apache.spark.sql.api.java.UDF1;

public class Pluspj implements UDF1<String ,String> {
    public String call(String s){
        return "PJ"+s;
    }
}
