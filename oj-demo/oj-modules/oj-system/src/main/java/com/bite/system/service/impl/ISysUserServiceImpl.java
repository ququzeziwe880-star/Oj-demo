package com.bite.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bite.common.core.domain.R;
import com.bite.common.core.enums.ResultCode;
import com.bite.common.core.enums.UserIdentity;
import com.bite.common.security.exception.ServiceException;
import com.bite.common.security.service.TokenService;
import com.bite.system.domain.SysUser;
import com.bite.system.domain.dto.SysUserSaveDTO;
import com.bite.system.mapper.SysUserMapper;
import com.bite.system.service.ISysUserService;
import com.bite.system.utils.BCryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RefreshScope
public class ISysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private TokenService tokenService;
    @Override
    public R<String> login(String userAccount, String password) {

        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        SysUser sysUser = (SysUser) sysUserMapper.selectOne(queryWrapper.select(SysUser::getUserId,SysUser::getPassword)
                .eq(SysUser::getUserAccount, userAccount));
        //R loginResult = new R();

        if (sysUser == null){
            /*loginResult.setCode(ResultCode.FAILED_USER_NOT_EXISTS.getCode());
            loginResult.setMsg(ResultCode.FAILED_USER_NOT_EXISTS.getMsg());
            return loginResult;*/
            return R.fail(ResultCode.FAILED_USER_NOT_EXISTS);
        }

        if (BCryptUtils.matchesPassword(password,sysUser.getPassword())) {
           /* loginResult.setCode(ResultCode.SUCCESS.getCode());
            loginResult.setMsg(ResultCode.SUCCESS.getMsg());
            return loginResult;*/
            return R.ok(tokenService.createToken(sysUser.getUserId(),secret,UserIdentity.ADMIN.getValue()));
        }

       /* loginResult.setCode(ResultCode.FAILED_LOGIN.getCode());
        loginResult.setMsg(ResultCode.FAILED_LOGIN.getMsg());
        return loginResult;*/
        return R.fail(ResultCode.FAILED_LOGIN);
    }

    @Override
    public int add(SysUserSaveDTO sysUserSaveDTO) {
        // 将dto转为实体
        List<SysUser> sysUserList = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserAccount, sysUserSaveDTO.getUserAccount()));
        if (CollectionUtil.isNotEmpty(sysUserList)){
            throw new ServiceException(ResultCode.AILED_USER_EXISTS);
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserAccount(sysUserSaveDTO.getUserAccount());
        sysUser.setPassword(BCryptUtils.encryptPassword(sysUserSaveDTO.getPassword()));
        return sysUserMapper.insert(sysUser);
    }
}
