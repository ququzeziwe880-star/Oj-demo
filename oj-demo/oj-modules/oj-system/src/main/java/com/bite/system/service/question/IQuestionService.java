package com.bite.system.service.question;

import com.bite.common.core.domain.R;
import com.bite.system.domain.question.dto.QuestionAddDTO;
import com.bite.system.domain.question.dto.QuestionEditDTO;
import com.bite.system.domain.question.dto.QuestionQueryDTO;
import com.bite.system.domain.question.vo.QuestionDetailVO;
import com.bite.system.domain.question.vo.QuestionVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IQuestionService {
    List<QuestionVO> list(QuestionQueryDTO questionQueryDTO);

    boolean add(QuestionAddDTO questionAddDTO);

    QuestionDetailVO detail(Long questionId);

    int edit(QuestionEditDTO questionEditDTO);

    int delete(Long questionId);
}
