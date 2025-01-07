import com.Test.HD.demo.Dog;
import com.Test.HD.demo.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestJson {
    @Test
    public void testWriteJson() throws JsonProcessingException {
        //实例化 Person对象 将Person对象转换为ISON申
        Dog dog=new Dog("小黄");
        Person person=new Person( "张三",10,dog);
        // 将Person对象转换成一个字符申
        ObjectMapper objectMapper =new ObjectMapper();
        String personstr =objectMapper.writeValueAsString(person);
        System.out.println(personstr);
    }

    @Test
    public void testReadJson() {
        String personStr = "{\"name\":\"张三\",\"age\":10,\"dog\":{\"name\":\"小黄\"}}";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Person person = objectMapper.readValue(personStr, Person.class);
            System.out.println(person);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    //MAP转JSON
    @Test
    public void testMapToJson() throws Exception{

            Map data =new HashMap();
            data.put("a","valuea");
            data.put("b","valueb");
            ObjectMapper objectMapper =new ObjectMapper();
            String s=objectMapper.writeValueAsString(data);
            System.out.println(s);
    }

    //arraylist转JSON
    @Test
    public void testListToJson() throws Exception{
        //实例化 Person对象 将Person对象转换为ISON申
        Dog dog=new Dog("小黄");
        Person person=new Person( "张三",10,dog);
        List<Person> list =new ArrayList<>();
        list.add(person);
        list.add(person);
        // 将Person对象转换成一个字符申
        ObjectMapper objectMapper =new ObjectMapper();
        String personstr =objectMapper.writeValueAsString(list);
        System.out.println(personstr);
    }
}
