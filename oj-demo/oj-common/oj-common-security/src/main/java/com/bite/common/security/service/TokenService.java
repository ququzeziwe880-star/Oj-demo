package com.bite.common.security.service;

import cn.hutool.core.lang.UUID;
import com.bite.common.core.constans.CacheConstans;
import com.bite.common.core.constans.JwtConstans;
import com.bite.common.redis.service.RedisService;
import com.bite.common.core.domain.LoginUser;
import com.bite.common.core.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
// 所有操作用户登录令牌的方法
@Service
@Slf4j
public class TokenService {

    @Autowired
    private RedisService redisService;

    public String createToken(Long userId,String secret,Integer identity){
        Map<String, Object> claims = new HashMap<>();
        String userKey = UUID.fastUUID().toString();
        claims.put(JwtConstans.LOGIN_USER_ID, userId);
        claims.put(JwtConstans.LOGIN_USER_KEY,userKey);
        String token = JwtUtils.createToken(claims, secret);
        // 第三方机制存储敏感信息
        //全局唯一id生成
        String tokenKey = getTokenKey(userKey);
        LoginUser loginUser = new LoginUser();
        loginUser.setIdentity(identity);
        redisService.setCacheObject(tokenKey,loginUser,CacheConstans.EXP, TimeUnit.MINUTES);
        return token;
    }

    //实际上延长token的有效时间就是延长redis中存储的用于用户认证的敏感信息的有效时间      操作redis   token ---> 唯一标识
    //在身份认证通过之后才会调用，并且在请求到达 controller 之后
    public void extendToken(String token,String secret){
        Claims claims;
        try {
            claims = JwtUtils.parseToken(token, secret); //获取令牌中信息 解析payload中信息
            if (claims == null) {
                return;
            }
        } catch (Exception e) {
            log.error("解析token:{},出现异常" ,token, e);
            return;
        }

        String userKey = JwtUtils.getUserKey(claims); //获取jwt中的key
        String tokenKey = getTokenKey(userKey);

        // 720min 12h  剩余 180min 的时候再续杯
        Long expire = redisService.getExpire(tokenKey, TimeUnit.MINUTES);
        if (expire != null && expire < CacheConstans.REFRESH_TIME){
            redisService.expire(tokenKey,CacheConstans.EXP,TimeUnit.MINUTES);
        }
    }

    private String getTokenKey(String userKey){
        return CacheConstans.Login_Token_Key + userKey;
    }
}
