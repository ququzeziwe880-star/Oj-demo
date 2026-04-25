package com.bite.system.test;

import com.bite.common.core.domain.R;
import com.bite.common.core.enums.ResultCode;
import com.bite.common.redis.service.RedisService;
import com.bite.system.domain.SysUser;
import com.bite.system.test.domain.LoginTestDTO;
import com.bite.system.test.domain.ValidationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bite.system.test.service.ITestService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Slf4j
@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private ITestService testService;

    @Autowired
    private RedisService redisService;
    @GetMapping("/list")
    public List<?> list(){
        return testService.list();
    }

    @GetMapping("apifoxtest")
    public R<String> apifoxtest(String apiId){
        R<String> result = new R<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData("testService" + apiId);
        return result;
    }

    @GetMapping("apifoxPost")
    public R<String> apifoxPost(@RequestBody LoginTestDTO loginTestDTO){
        R<String> result = new R<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData("apifoxPost:" + loginTestDTO.getUserAccount() + ":" + loginTestDTO.getPassword());
        return result;
    }

    @GetMapping("/log")
    public String log(){
        log.info("我是info级别日志");
        log.error("我是error级别日志");
        return "日志测试";
    }

    @GetMapping("/redisAddAndGet")
    public String redisAddAndGet(){
        SysUser sysUser = new SysUser();
        sysUser.setUserAccount("redisTest");
        redisService.setCacheObject("u",sysUser);
        SysUser us = redisService.getCacheObject("u",SysUser.class);
        return us.toString();
    }

    public static void main(String[] args) {
        Map<String,Integer> hash = new HashMap<>();
        for (Map.Entry<String,Integer> pair: hash.entrySet());
        PriorityQueue heap = new PriorityQueue();
    }

    @GetMapping("/validation")
    public String validation(@Validated ValidationDTO validationDTO){
        return "参数测试";
    }
}
