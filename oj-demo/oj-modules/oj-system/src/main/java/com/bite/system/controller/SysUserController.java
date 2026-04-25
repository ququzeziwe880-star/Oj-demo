package com.bite.system.controller;

import com.bite.common.core.domain.R;
import com.bite.system.domain.LoginDTO;
import com.bite.system.domain.SysUser;
import com.bite.system.domain.SysUserSaveDTO;
import com.bite.system.domain.SysUserVO;
import com.bite.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sysUser")
@Tag(name = "管理员api")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    @PostMapping("/login")
    @Operation(summary = "管理员登陆",description = "根据账号密码登录")
    @ApiResponse(responseCode = "1000",description = "操作成功")
    @ApiResponse(responseCode = "2000",description = "服务器繁忙请稍后重试")
    @ApiResponse(responseCode = "3102",description = "用户不存在")
    @ApiResponse(responseCode = "3103",description = "用户名或密码错误")
    public R<Void> login(@RequestBody LoginDTO loginDTO){
        return sysUserService.login(loginDTO.getUserAccount(),loginDTO.getPassword());
    }


    //管理员增删改查

    //新增
    @PostMapping("/add")
    @Operation(summary = "新增管理员",description = "根据提供的信息新增管理员")
    @ApiResponse(responseCode = "1000",description = "操作成功")
    @ApiResponse(responseCode = "2000",description = "服务器繁忙请稍后重试")
    @ApiResponse(responseCode = "3101",description = "用户已存在")
    public R<Void> add(@RequestBody SysUserSaveDTO sysUserSaveDTO){
        return null;
    }



    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "通过用户id删除用户")
    @Parameters(value = {
            @Parameter(name = "userId", in = ParameterIn.PATH, description = "用 户ID")
    })
    @ApiResponse(responseCode = "1000", description = "成功删除用户")
    @ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
    @ApiResponse(responseCode = "3101", description = "用户不存在")
    public R<Void> delete(@PathVariable Long userId) {
        return null;
    }



    @Operation(summary = "用户详情", description = "根据查询条件查询用户详情")
    @GetMapping("/detail")
    @Parameters(value = {
            @Parameter(name = "userId", in = ParameterIn.QUERY, description =
                    "用户ID"),
            @Parameter(name = "sex", in = ParameterIn.QUERY, description = "用户           性别")
    })
    @ApiResponse(responseCode = "1000", description = "成功获取用户信息")
    @ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
    @ApiResponse(responseCode = "3101", description = "用户不存在")
    public R<SysUserVO> detail(Long userId, @RequestParam(required = false)
    String sex) {
        return null;
    }
}

