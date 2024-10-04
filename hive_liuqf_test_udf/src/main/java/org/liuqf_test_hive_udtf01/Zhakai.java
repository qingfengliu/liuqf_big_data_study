package org.liuqf_test_hive_udtf01;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.util.ArrayList;
import java.util.List;

//功能：将输入的字符串按照8个字符切分
public class Zhakai extends GenericUDTF {

    private List<String> list = new ArrayList<String>();
    @Override
    public void close() {
        // TODO Auto-generated method stub
    }

    @Override
    public StructObjectInspector initialize(ObjectInspector[] arg0) throws UDFArgumentException {
        // TODO Auto-generated method stub

        //输出数据的列名
        ArrayList<String> fieldNames = new ArrayList<String>();
        //列名取名
        fieldNames.add("word");

        //输出数据的类型
        ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
        //根据将来的返回值确定返回的类型
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        //fieldNames定义了输出的列名，fieldOIs定义了返回的类型
        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
    }

    //处理输入数据
    @Override
    public void process(Object[] args) throws HiveException {
        String data = args[0].toString();
        //处理一个字符串,每5个字符切分一次
        for (int i = 0; i < data.length(); i += 8) {
            //切分字符串
            if (i + 5 < data.length()) {
                list.add(data.substring(i, i + 8));
            } else {
                list.add(data.substring(i));
            }
        }

        //输出数据
        for (String str : list) {
            //输出一行数据
            String[] result = new String[1];
            result[0] = str;
            forward(result);
        }
        list.clear();
    }

}
