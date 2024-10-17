package org.example;

import org.json.JSONObject;

import java.util.Iterator;

public class Test2 {
    public static void main(String[] args) {

        JSONObject jsonObject1 = new JSONObject("{\"name\": \"Reed\",\"A\":\"1\",\"timestamp\":10}");

        JSONObject jsonObject2 = new JSONObject("{\"name\": \"Reed\",\"B\":\"2\",\"timestamp\":20}");


        Iterator<String> keys = jsonObject2.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            //字符串
            if (jsonObject1.has(key)&& !key.equals("name")) {
                jsonObject1.put(key + "_A", jsonObject1.get(key));
                jsonObject1.put(key + "_B", jsonObject2.get(key));
                //删除key
                jsonObject1.remove(key);
            } else {
                jsonObject1.put(key, jsonObject2.get(key));
            }
        }
        System.out.println(jsonObject1.toString());
    }
}
