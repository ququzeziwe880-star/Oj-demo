package com.bite.common.core.utils;

import com.bite.common.core.constans.JwtConstans;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Map;

public class JwtUtils {
    /**
     * 生成令牌
     *
     * @param claims 数据
     * @param secret 密钥
     * @return 令牌
     */
    public static String createToken(Map<String, Object> claims, String secret)
    {
        String token = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }
    /**
     * 从令牌中获取数据
     *
     * @param token 令牌
     * @param secret 密钥
     * @return 数据
     */
    public static Claims parseToken(String token, String secret) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private static String toStr(Object value) {
        if (value == null){
            return "";
        }
        return value.toString();
    }

    public static String getUserKey(Claims claims) {
        return toStr(claims.get(JwtConstans.LOGIN_USER_KEY));
    }

    public static String getUserId(Claims claims) {
        return toStr(claims.get(JwtConstans.LOGIN_USER_ID));
    }

    public static void main(String[] args) {
       /* Map<String, Object> claims = new HashMap<>();
        claims.put("userId",123456789L);
        System.out.println(createToken(claims, "dhjewaiojdiowajhodifjdewaiokhfioa"));*/
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjEyMzQ1Njc4OX0.troiCZDuE3wH7aJYpSUbRDw6AOhU7HNLzc5KM1Qi9SN59Iovnse7kUubQ6WIQsS0IDYNC6SRjxR6DQo9voq8TA";
        Claims claim = parseToken(token, "dhjewaiojdiowajhodifjdewaiokhfioa");
        System.out.println(claim.toString());
    }
}
