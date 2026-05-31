package com.bite.friend.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bite.common.core.domain.TableDataInfo;
import com.bite.friend.domain.question.Question;
import com.bite.friend.domain.question.dto.QuestionQueryDTO;
import com.bite.friend.domain.question.es.QuestionES;
import com.bite.friend.domain.question.vo.QuestionVO;
import com.bite.friend.elasticsearch.QuestionRepository;
import com.bite.friend.mapper.question.QuestionMapper;
import com.bite.friend.service.question.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class QuestionService implements IQuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        long count = questionRepository.count();
        if (count <= 0){
            refreshQuestion();
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(questionQueryDTO.getPageNum() - 1,questionQueryDTO.getPageSize(),sort);
        Integer difficulty = questionQueryDTO.getDifficulty();
        String keyword = questionQueryDTO.getKeyword();
        Page<QuestionES> questionESPage;
        if (difficulty == null && StrUtil.isEmpty(keyword)){
            questionESPage = questionRepository.findAll(pageable);
        } else if (StrUtil.isEmpty(keyword)) {
            questionESPage = questionRepository.findQuestionByDifficulty(difficulty,pageable);
        } else if (difficulty == null) {
            questionESPage = questionRepository.findByTitleOrContent(keyword,keyword,pageable);
        }else{
            questionESPage = questionRepository.findByTitleOrContentAndDifficulty(keyword,keyword,difficulty,pageable);
        }
        long total = questionESPage.getTotalElements();
        if (total <= 0){
            return TableDataInfo.empty();
        }
        List<QuestionES> questionESList = questionESPage.getContent();
        List<QuestionVO> questionVOList = BeanUtil.copyToList(questionESList, QuestionVO.class);
        return TableDataInfo.success(questionVOList,total);
    }

    private void refreshQuestion() {
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>());
        if (CollectionUtils.isEmpty(questionList)){
            return;
        }
        List<QuestionES> questionES = BeanUtil.copyToList(questionList, QuestionES.class);
        questionRepository.saveAll(questionES);
    }
}
