package org.liuqf_test_hive_udf01;

import org.apache.hadoop.hive.ql.exec.UDF;

public class Main extends UDF {
    //将字符串前边加上PJ
    public String evaluate(String str) {
        return "PJ" + str;
    }

}
