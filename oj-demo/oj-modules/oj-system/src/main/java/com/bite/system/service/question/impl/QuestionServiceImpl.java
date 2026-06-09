package com.bite.system.service.question.impl;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bite.common.core.constans.Constants;
import com.bite.common.core.enums.ResultCode;
import com.bite.common.security.exception.ServiceException;
import com.bite.system.domain.question.Question;
import com.bite.system.domain.question.dto.QuestionAddDTO;
import com.bite.system.domain.question.dto.QuestionEditDTO;
import com.bite.system.domain.question.dto.QuestionQueryDTO;
import com.bite.system.domain.question.es.QuestionES;
import com.bite.system.domain.question.vo.QuestionDetailVO;
import com.bite.system.domain.question.vo.QuestionVO;
import com.bite.system.elasticsearch.QuestionRepository;
import com.bite.system.manager.QuestionCacheManager;
import com.bite.system.mapper.question.QuestionMapper;
import com.bite.system.service.question.IQuestionService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionCacheManager questionCacheManager;
    @Override
    public List<QuestionVO> list(QuestionQueryDTO questionQueryDTO) {
        String excludeIdStr = questionQueryDTO.getExcludeIdStr();
        if (StrUtil.isNotEmpty(excludeIdStr)){
            String[] excludeIdArr = excludeIdStr.split(Constants.SPLIT_SEM);
            Set<Long> excludeIdSet = Arrays.stream(excludeIdArr)
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
            questionQueryDTO.setExcludeIdSet(excludeIdSet);
        }
        PageHelper.startPage(questionQueryDTO.getPageNum(),questionQueryDTO.getPageSize());
        return questionMapper.selectQuestionList(questionQueryDTO);
    }

    @Override
    public boolean add(QuestionAddDTO questionAddDTO) {
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>().eq(Question::getTitle, questionAddDTO.getTitle()));
        if (CollectionUtil.isNotEmpty(questions)){
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }

        Question question = new Question();
        BeanUtil.copyProperties(questionAddDTO,question);
        int insert = questionMapper.insert(question);
        if (insert <= 0){
            return false;
        }
        QuestionES questionES = new QuestionES();
        BeanUtil.copyProperties(question,questionES);
        questionRepository.save(questionES);
        questionCacheManager.addCache(questionES.getQuestionId());
        return true;
    }

    @Override
    public QuestionDetailVO detail(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null){
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }

        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        BeanUtil.copyProperties(question,questionDetailVO);
        return questionDetailVO;
    }

    @Override
    public int edit(QuestionEditDTO questionEditDTO) {
        Question question = questionMapper.selectById(questionEditDTO.getQuestionId());
        if (question == null){
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }

        question.setTitle(questionEditDTO.getTitle());
        question.setDifficulty(questionEditDTO.getDifficulty());
        question.setTimeLimit(questionEditDTO.getTimeLimit());
        question.setSpaceLimit(questionEditDTO.getSpaceLimit());
        question.setContent(questionEditDTO.getContent());
        question.setQuestionCase(questionEditDTO.getQuestionCase());
        question.setDefaultCode(questionEditDTO.getDefaultCode());
        question.setMainFuc(questionEditDTO.getMainFuc());

        QuestionES questionES = new QuestionES();
        BeanUtil.copyProperties(question,questionES);
        questionRepository.save(questionES);
        return questionMapper.updateById(question);
    }

    @Override
    public int delete(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null){
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        questionRepository.deleteById(questionId);
        questionCacheManager.deleteCache(questionId);
        return questionMapper.deleteById(questionId);
    }
}
