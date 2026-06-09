package com.bite.system.manager;

import com.bite.common.core.constans.CacheConstants;
import com.bite.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionCacheManager {
    @Autowired
    private RedisService redisService;

    public void addCache(Long questionId){
        redisService.leftPushForList(CacheConstants.QUESTION_LIST,questionId);
    }

    public void deleteCache(Long questionId){
        redisService.removeForList(CacheConstants.QUESTION_LIST,questionId);
    }
}
