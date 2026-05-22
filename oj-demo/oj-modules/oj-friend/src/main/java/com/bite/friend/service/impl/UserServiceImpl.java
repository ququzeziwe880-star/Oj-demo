package com.bite.friend.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bite.common.core.constans.CacheConstans;
import com.bite.common.core.constans.Constans;
import com.bite.common.core.domain.R;
import com.bite.common.core.enums.ResultCode;
import com.bite.common.core.enums.UserIdentity;
import com.bite.common.core.enums.UserStatus;
import com.bite.common.message.service.AliSmsService;
import com.bite.common.redis.service.RedisService;
import com.bite.common.security.exception.ServiceException;
import com.bite.common.security.service.TokenService;
import com.bite.friend.domain.User;
import com.bite.friend.domain.dto.UserDTO;
import com.bite.friend.mapper.UserMapper;
import com.bite.friend.service.IUserService;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private AliSmsService aliSmsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${sms.code-expiration:5}")
    private Long phoneCodeExpiration;
    //开关验证码
    @Value("${sms.is-send:false}")
    private boolean isSend;
    @Value("${sms.send-limit:3}")
    private Integer sendLimit;


    @Override
    public boolean sendCode(UserDTO userDTO) {
        if (!checkPhone(userDTO.getPhone())){
            throw new ServiceException(ResultCode.FAILED_USER_PHONE);
        }

        String phoneCodeKey = getPhoneCodeKey(userDTO.getPhone());
        Long expire = redisService.getExpire(phoneCodeKey, TimeUnit.MILLISECONDS);
        if (expire != null && (phoneCodeExpiration * 60 - expire) < 60){
            throw new ServiceException(ResultCode.FAILED_FREQUENT);
        }
        String codeTimeKey = getCodeTimeKey(userDTO.getPhone());
        Long sendTimes = redisService.getCacheObject(codeTimeKey, Long.class);
        if (sendTimes != null && sendTimes >= sendLimit){
            throw new ServiceException(ResultCode.FAILED_TIME_LIMIT);
        }

        String code = isSend ? RandomUtil.randomNumbers(6) : Constans.DEFAULT_CODE;
        redisService.setCacheObject(getPhoneCodeKey(userDTO.getPhone()),code,phoneCodeExpiration, TimeUnit.MINUTES);
        if (isSend){
            boolean sendMobileCode = aliSmsService.sendMobileCode(userDTO.getPhone(), code);
            if (!sendMobileCode){
                throw new ServiceException(ResultCode.FAILED_SEND_CODE);
            }
        }
        redisService.increament(codeTimeKey);
        if (sendTimes == null){
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0));
            redisService.expire(codeTimeKey,seconds,TimeUnit.SECONDS);
        }
        return true;
    }

    @Override
    public String codeLogin(String phone, String code) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        checkCode(phone, code);
        if (user == null){
            user = new User();
            user.setPhone(phone);
            user.setStatus(UserStatus.Normal.getValue());
            userMapper.insert(user);
        }
        return tokenService.createToken(user.getUserId(),
                secret, UserIdentity.ORDINARY.getValue(),user.getNickName());
    }

    private void checkCode(String phone, String code) {
        String phoneCodeKey = getPhoneCodeKey(phone);
        String cacheCode = redisService.getCacheObject(phoneCodeKey, String.class);
        if (StrUtil.isEmpty(cacheCode)){
            throw new ServiceException(ResultCode.FAILED_INVALID_CODE);
        }
        if (!cacheCode.equals(code)){
            throw new ServiceException(ResultCode.FAILED_ERROR_CODE);
        }
        redisService.deleteObject(phoneCodeKey);
    }

    private String getPhoneCodeKey(String phone) {
        return CacheConstans.Phone_Code_Key + phone;
    }

    private String getCodeTimeKey(String phone) {
        return CacheConstans.Code_Time_Key + phone;
    }

    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }
}
