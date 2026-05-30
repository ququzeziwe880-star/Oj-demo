package com.bite.system.service.sysuser.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bite.common.core.constans.HttpConstants;
import com.bite.common.core.domain.LoginUser;
import com.bite.common.core.domain.R;
import com.bite.common.core.domain.vo.LoginUserVO;
import com.bite.common.core.enums.ResultCode;
import com.bite.common.core.enums.UserIdentity;
import com.bite.common.security.exception.ServiceException;
import com.bite.common.security.service.TokenService;
import com.bite.system.domain.sysuser.SysUser;
import com.bite.system.domain.sysuser.dto.SysUserSaveDTO;
import com.bite.system.mapper.sysuser.SysUserMapper;
import com.bite.system.service.sysuser.ISysUserService;
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
        SysUser sysUser = (SysUser) sysUserMapper.selectOne(queryWrapper
                .select(SysUser::getUserId,SysUser::getPassword,SysUser::getNickName)
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
            return R.ok(tokenService.createToken(sysUser.getUserId(),
                    secret,UserIdentity.ADMIN.getValue(),sysUser.getNickName(),null));
        }

       /* loginResult.setCode(ResultCode.FAILED_LOGIN.getCode());
        loginResult.setMsg(ResultCode.FAILED_LOGIN.getMsg());
        return loginResult;*/
        return R.fail(ResultCode.FAILED_LOGIN);
    }


    @Override
    public boolean logout(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return tokenService.deleteLoginUser();
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

    @Override
    public R<LoginUserVO> info(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        LoginUser loginUser = tokenService.getLoginUser();

        if (loginUser == null) return R.fail();

        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setNickName(loginUser.getNickName());
        return R.ok(loginUserVO);
    }

}
