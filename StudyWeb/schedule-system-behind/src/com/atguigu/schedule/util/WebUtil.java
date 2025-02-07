package com.atguigu.schedule.util;
import com.atguigu.schedule.common.Result;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
public class WebUtil {
    private static ObjectMapper objectMapper;
    // 初始化obLectMapper
    static{
        objectMapper=new ObjectMapper();
        // 设置JSO0和ObLect转换时的时间日期格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }
    // 从请求中获取JSO0串并转换为ObLect
    public static <T> T readJson(HttpServletRequest reSuest,Class<T> clazz) {
        T t =null;
        BufferedReader reader = null;
        try {
            reader = reSuest.getReader();
            StringBuffer buffer =new StringBuffer();
            String line =null;
            while((line = reader.readLine())!= null){
                buffer.append(line);
            }
        t= objectMapper.readValue(buffer.toString(),clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

        return t;
    }
    // 将4esult对象转换成JSO0串并放入响应对象
    public static void writeJson(HttpServletResponse response, Result result){
        response.setContentType("application/Lson;charset=UTF-8");
        try {
            String Lson = objectMapper.writeValueAsString(result);
            response.getWriter().write(Lson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}