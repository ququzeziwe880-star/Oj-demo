package com.bite.friend.componet;

import com.bite.common.core.constans.Constants;
import com.bite.common.core.enums.ResultCode;
import com.bite.common.core.utils.ThreadLocalUtil;
import com.bite.common.security.exception.ServiceException;
import com.bite.friend.domain.user.vo.UserVO;
import com.bite.friend.manager.UserCacheManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Aspect
public class checkUserStatusAspect {

    @Autowired
    private UserCacheManager userCacheManager;

    @Before(value = "@annotation(com.bite.friend.componet.CheckUserStatus))")
    public void checkUserStatus() throws Throwable {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID,Long.class);
        UserVO user = userCacheManager.getUserById(userId);
        if (user == null){
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if (Objects.equals(user.getStatus(),Constants.FALSE)){
            throw new ServiceException(ResultCode.FAILED_USER_BANNED);
        }
    }
}
