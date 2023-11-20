package com.ss.shoppingweb.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {
    // 有效期
    private static Long expire = 24*60*60*1000L;  //24小时
    // 令牌秘钥
    private static  String signKey = "123456";

    /**
     * 生成JWT令牌
     * */
    public static String generateJwt(Map<String,Object> claims){
        String jwt= Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256,signKey)
                .setExpiration(new Date(System.currentTimeMillis()+expire))
                .compact();
        return jwt;
    }

    /**
     * 解析JWT令牌
     * */
    public static Claims parseJwt(String jwt){
        System.out.println("---------------");
        Claims claims=Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }

    /**
     * 获取JWT令牌获取"role"
     * */
    public static String getJwtRole(String token){
        Claims claims=parseJwt(token);
        String role = claims.get("role", String.class);
        return role;
    }

    /**
     * 解析JWT令牌获取"name"
    */
    public static String getJwtName(String token){
        Claims claims=parseJwt(token);
        String name = claims.get("name", String.class);
        return name;
    }

    /**
     * 解析JWT令牌获取"id"
     */
    public static Integer getJwtId(String token){
        Claims claims=parseJwt(token);
        Integer id=claims.get("id", Integer.class);
        return id;
    }
}
