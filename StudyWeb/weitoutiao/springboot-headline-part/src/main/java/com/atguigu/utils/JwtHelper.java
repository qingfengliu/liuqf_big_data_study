package com.atguigu.utils;

import com.alibaba.druid.util.StringUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Component
@ConfigurationProperties(prefix = "jwt.token")
public class JwtHelper {

    private  long tokenExpiration; //有效时间,单位毫秒 1000毫秒 == 1秒
    private  String tokenSignKey;  //当前程序签名秘钥
    private SecretKey key;

    @PostConstruct
    public void init(){
        key = Keys.hmacShaKeyFor(tokenSignKey.getBytes(StandardCharsets.UTF_8));
    }

    //生成token字符串
    public  String createToken(Long userId) {
        System.out.println("tokenExpiration = " + tokenExpiration);
        System.out.println("tokenSignKey = " + tokenSignKey);
        return Jwts.builder()
                .setSubject("user")
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration*1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //从token字符串获取userid
    public  Long getUserId(String token) {
        //修改一下时间戳过期不报异常。
        //解密
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        System.out.println(token);
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.get("userId").toString());
    }



    //判断token是否有效
    public  boolean isExpiration(String token){
        //验证token是否过期
        try {
            Date tokendate = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            boolean isExpire = tokendate.before(new Date());

            return isExpire;
        } catch (Exception e) {
            System.out.println(e);
            return true;
        }
    }
}
