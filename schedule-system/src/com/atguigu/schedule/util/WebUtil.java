package com.atguigu.schedule.util;
import com.atguigu.schedule.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WebUtil {
    private static ObjectMapper objectMapper;
    //初始化objectMapper
    static {
        objectMapper = new ObjectMapper();
        //设置JSON和0bject转换时的时间日期格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    //从请求中获取JSON串并转换为0bject
    public static <T>T readJson(HttpServletRequest request, Class<T> clazz){
        T t =null;
        BufferedReader reader=null;
        try {
            reader = request.getReader();
            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            t = objectMapper.readValue(buffer.toString(), clazz);
        }catch(IOException e){
                throw new RuntimeException(e);
        }
        return t;
    }

    //专门用于向客户端响应json数据
    public static void writeJson(HttpServletResponse response, Result result) {
        response.setContentType("application/json;charset=utf-8");
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(response.getOutputStream(), result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
