package com.bite.system.test;

import com.bite.common.core.domain.R;
import com.bite.common.core.enums.ResultCode;
import com.bite.system.domain.LoginDTO;
import com.bite.system.test.domain.LoginTestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bite.system.test.service.ITestService;

import java.util.List;

@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private ITestService testService;

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
}
