package com.bite.system.manager;

import com.bite.common.core.constans.CacheConstants;
import com.bite.common.redis.service.RedisService;

import com.bite.system.domain.user.User;
import com.bite.system.mapper.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class UserCacheManager {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserMapper userMapper;

    public void updateStatus(Long userId, Integer status) {
        String userKey = getUserKey(userId);
        User user = redisService.getCacheObject(userKey, User.class);
        if (user == null){
            user = userMapper.selectById(userId);
        }
        user.setStatus(status);
        //刷新用户缓存
        redisService.setCacheObject(userKey, user);
        //设置用户缓存有效期为10分钟
        redisService.expire(userKey, CacheConstants.USER_EXP, TimeUnit.MINUTES);
    }

    //u:d:用户id
    private String getUserKey(Long userId) {
        return CacheConstants.USER_DETAIL + userId;
    }

}
