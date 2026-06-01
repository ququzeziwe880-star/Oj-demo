package com.bite.friend.service.user.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bite.common.core.constans.CacheConstants;
import com.bite.common.core.constans.Constants;
import com.bite.common.core.constans.HttpConstants;
import com.bite.common.core.domain.LoginUser;
import com.bite.common.core.domain.R;
import com.bite.common.core.domain.vo.LoginUserVO;
import com.bite.common.core.enums.ResultCode;
import com.bite.common.core.enums.UserIdentity;
import com.bite.common.core.enums.UserStatus;
import com.bite.common.core.utils.ThreadLocalUtil;
import com.bite.common.message.service.AliSmsService;
import com.bite.common.redis.service.RedisService;
import com.bite.common.security.exception.ServiceException;
import com.bite.common.security.service.TokenService;
import com.bite.friend.domain.user.User;
import com.bite.friend.domain.user.dto.UserDTO;
import com.bite.friend.domain.user.dto.UserUpdateDTO;
import com.bite.friend.domain.user.vo.UserVO;
import com.bite.friend.manager.UserCacheManager;
import com.bite.friend.mapper.user.UserMapper;
import com.bite.friend.service.user.IUserService;
import org.jetbrains.annotations.NotNull;
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
    private UserCacheManager userCacheManager;

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

    @Value("${file.oss.downLoadUrl}")
    private String downLoadUrl;
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

        String code = isSend ? RandomUtil.randomNumbers(6) : Constants.DEFAULT_CODE;
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
                secret, UserIdentity.ORDINARY.getValue(),user.getNickName(),user.getHeadImage());
    }

    @Override
    public boolean logout(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return tokenService.deleteLoginUser();
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
        if (StrUtil.isNotEmpty(loginUser.getHeadImage())){
            loginUserVO.setHeadImage( downLoadUrl + loginUser.getHeadImage());
        }
        return R.ok(loginUserVO);
    }

    @Override
    public UserVO detail() {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID,Long.class);
        if (userId == null){
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        UserVO userVO = userCacheManager.getUserById(userId);
        if (userVO == null){
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if (StrUtil.isNotEmpty(userVO.getHeadImage())){
            userVO.setHeadImage( downLoadUrl + userVO.getHeadImage());
        }
        return userVO;
    }

    @Override
    public int edit(UserUpdateDTO userUpdateDTO) {

        User user = getUser();
        user.setNickName(userUpdateDTO.getNickName());
        user.setSex(userUpdateDTO.getSex());
        user.setSchoolName(userUpdateDTO.getSchoolName());
        user.setMajorName(userUpdateDTO.getMajorName());
        user.setPhone(userUpdateDTO.getPhone());
        user.setEmail(userUpdateDTO.getEmail());
        user.setWechat(userUpdateDTO.getWechat());
        user.setIntroduce(userUpdateDTO.getIntroduce());

        userCacheManager.refreshUser(user);
        tokenService.refreshLoginUser(user.getNickName(),user.getHeadImage(),ThreadLocalUtil.get(Constants.USER_KEY,String.class));
        return userMapper.updateById(user);
    }


    @NotNull
    private User getUser() {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID,Long.class);
        if (userId == null){
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        return user;
    }

    @Override
    public int updateHeadImage(String headImage) {

        User user = getUser();
        user.setHeadImage(headImage);

        userCacheManager.refreshUser(user);
        tokenService.refreshLoginUser(user.getNickName(),user.getHeadImage(),ThreadLocalUtil.get(Constants.USER_KEY,String.class));
        return userMapper.updateById(user);
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
        return CacheConstants.Phone_Code_Key + phone;
    }

    private String getCodeTimeKey(String phone) {
        return CacheConstants.Code_Time_Key + phone;
    }

    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[2|3|4|5|6|7|8|9][0-9]\\d{8}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }
}
