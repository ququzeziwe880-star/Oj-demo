package com.bite.friend.service.exam.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bite.common.core.constans.Constants;
import com.bite.common.core.domain.TableDataInfo;
import com.bite.common.core.enums.ExamListType;
import com.bite.common.core.enums.ResultCode;
import com.bite.common.core.utils.ThreadLocalUtil;
import com.bite.common.security.exception.ServiceException;
import com.bite.common.security.service.TokenService;
import com.bite.friend.componet.CheckUserStatus;
import com.bite.friend.domain.exam.Exam;
import com.bite.friend.domain.exam.dto.ExamDTO;
import com.bite.friend.domain.exam.dto.ExamQueryDTO;
import com.bite.friend.domain.exam.vo.ExamVO;
import com.bite.friend.domain.user.UserExam;
import com.bite.friend.domain.user.vo.UserVO;
import com.bite.friend.manager.ExamCacheManager;
import com.bite.friend.manager.UserCacheManager;
import com.bite.friend.mapper.exam.ExamMapper;
import com.bite.friend.mapper.user.UserExamMapper;
import com.bite.friend.service.exam.IUserExamService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserExamService implements IUserExamService {
    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private UserExamMapper userExamMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ExamCacheManager examCacheManager;


    @Value("${jwt.secret}")
    private String secret;

    @Override
    public int enter(String token, Long examId) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID,Long.class);
        Exam exam = examMapper.selectById(examId);
        if (exam == null){
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        if (exam.getStartTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.EXAM_STARTED);
        }
        //Long userId = tokenService.getUserId(token,secret);
        UserExam userExam = userExamMapper.selectOne(new LambdaQueryWrapper<UserExam>()
                .eq(UserExam::getExamId, examId)
                .eq(UserExam::getUserId, userId));
        if(userExam != null){
            throw new ServiceException(ResultCode.USER_EXAM_HAS_ENTER);
        }
        examCacheManager.addUserExamCache(userId,examId);
        userExam = new UserExam();
        userExam.setUserId(userId);
        userExam.setExamId(examId);
        return userExamMapper.insert(userExam);
    }



    @Override
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID,Long.class);
        examQueryDTO.setType(ExamListType.USER_EXAM_LIST.getValue());
        Long total = examCacheManager.getListSize(ExamListType.USER_EXAM_LIST.getValue(), userId);
        List<ExamVO> examVOList;
        if (total == null || total <= 0){
            PageHelper.startPage(examQueryDTO.getPageNum(),examQueryDTO.getPageSize());
            examVOList = userExamMapper.selectUserExamList(userId);
            examCacheManager.refreshCache(examQueryDTO.getType(),userId);
            total = new PageInfo<>(examVOList).getTotal();
        }else {
            examVOList = examCacheManager.getExamVOList(examQueryDTO,userId);
            total = examCacheManager.getListSize(examQueryDTO.getType(), userId);
        }
        if (CollectionUtils.isEmpty(examVOList)){
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(examVOList,total);
    }
}
