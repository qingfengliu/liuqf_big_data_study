package org.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

public class Test {
    public static void main(String[] args) {
        Student student=new Student("liuqf","beijing","123456","504772813@qq.com","alibaba","engineer");
        JSONObject jsonObject2 = new JSONObject(student);
        System.out.println(jsonObject2);
    }

    @Data  // 注解在类上，为类提供读写属性，还提供equals()、hashCode()、toString()方法
    @AllArgsConstructor  // 注解在类上，为类提供全参构造函数，参数的顺序与属性定义的顺序一致
    @NoArgsConstructor  // 注解在类上，为类提供无参构造函数
    public static class Student {
        private String name;
        private String address;
        private String phone;
        private String email;
        private String company;
        private String job;
    }
}
