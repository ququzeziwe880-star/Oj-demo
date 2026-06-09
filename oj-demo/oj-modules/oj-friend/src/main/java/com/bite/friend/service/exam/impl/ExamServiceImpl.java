package com.bite.friend.service.exam.impl;

import com.bite.common.core.constans.Constants;
import com.bite.common.core.domain.TableDataInfo;
import com.bite.common.core.utils.ThreadLocalUtil;
import com.bite.friend.domain.exam.dto.ExamQueryDTO;
import com.bite.friend.domain.exam.dto.ExamRankDTO;
import com.bite.friend.domain.exam.vo.ExamRankVO;
import com.bite.friend.domain.exam.vo.ExamVO;
import com.bite.friend.domain.user.vo.UserVO;
import com.bite.friend.manager.ExamCacheManager;
import com.bite.friend.manager.UserCacheManager;
import com.bite.friend.mapper.exam.ExamMapper;
import com.bite.friend.mapper.user.UserExamMapper;
import com.bite.friend.service.exam.IExamService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ConcurrentModificationException;
import java.util.List;


@Service
public class ExamServiceImpl implements IExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamCacheManager examCacheManager;

    @Autowired
    private UserCacheManager userCacheManager;

    @Autowired
    private UserExamMapper userExamMapper;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(),examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO) {
        Long total = examCacheManager.getListSize(examQueryDTO.getType(), null);
        List<ExamVO> examVOList;
        if (total == null || total <= 0){
            examVOList = list(examQueryDTO);
            examCacheManager.refreshCache(examQueryDTO.getType(),null);
            total = new PageInfo<>(examVOList).getTotal();
        }else {
            examVOList = examCacheManager.getExamVOList(examQueryDTO,null);
            total = examCacheManager.getListSize(examQueryDTO.getType(), null);
        }
        if (CollectionUtils.isEmpty(examVOList)){
            return TableDataInfo.empty();
        }
        assembleExamVOList(examVOList);
        return TableDataInfo.success(examVOList,total);
    }


    @Override
    public TableDataInfo rankList(ExamRankDTO examRankDTO) {
        Long total = examCacheManager.getRankListSize(examRankDTO.getExamId());
        List<ExamRankVO> examRankVOList;
        if (total == null || total <= 0){
            PageHelper.startPage(examRankDTO.getPageNum(),examRankDTO.getPageSize());
            examRankVOList = userExamMapper.selectExamRankList(examRankDTO.getExamId());
            examCacheManager.refreshExamRankCache(examRankDTO.getExamId());
            total = new PageInfo<>(examRankVOList).getTotal();
        }else {
            examRankVOList = examCacheManager.getExamRankList(examRankDTO);
        }
        if (CollectionUtils.isEmpty(examRankVOList)){
            return TableDataInfo.empty();
        }
        assembleExamRankVOList(examRankVOList);
        return TableDataInfo.success(examRankVOList,total);
    }

    private void assembleExamRankVOList(List<ExamRankVO> examRankVOList) {
        if (CollectionUtils.isEmpty(examRankVOList)){
            return;
        }
        for (ExamRankVO examRankVO : examRankVOList) {
            Long userId = examRankVO.getUserId();
            UserVO user = userCacheManager.getUserById(examRankVO.getUserId());
            examRankVO.setNickName(user.getNickName());
        }
    }


    @Override
    public String getFirstQuestion(Long examId) {
        Long examQuestionListSize = examCacheManager.getExamQuestionListSize(examId);
        if (examQuestionListSize == null || examQuestionListSize == 0){
            examCacheManager.refreshExamQuestionCache(examId);
        }
       return examCacheManager.getFirstQuestion(examId).toString();
    }

    @Override
    public String preQuestion(Long examId, Long questionId) {
        Long examQuestionListSize = examCacheManager.getExamQuestionListSize(examId);
        if (examQuestionListSize == null || examQuestionListSize == 0){
            examCacheManager.refreshExamQuestionCache(examId);
        }
        return examCacheManager.getPreExamQuestion(examId,questionId).toString();
    }

    @Override
    public String nextQuestion(Long examId, Long questionId) {
        Long examQuestionListSize = examCacheManager.getExamQuestionListSize(examId);
        if (examQuestionListSize == null || examQuestionListSize == 0){
            examCacheManager.refreshExamQuestionCache(examId);
        }
        return examCacheManager.getNextExamQuestion(examId,questionId).toString();
    }


    private void assembleExamVOList(List<ExamVO> examVOList) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        List<Long> userExamIdList = examCacheManager.getAllUserExamList(userId);
        if (CollectionUtils.isEmpty(userExamIdList)){
            return;
        }
        for(ExamVO examVO : examVOList){
            if (userExamIdList.contains(examVO.getExamId())){
                examVO.setEnter(true);
            }
        }
    }

}
