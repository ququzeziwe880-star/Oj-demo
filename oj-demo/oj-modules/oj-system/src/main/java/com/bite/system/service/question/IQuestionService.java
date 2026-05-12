package com.bite.system.service.question;

import com.bite.system.domain.question.dto.QuestionAddDTO;
import com.bite.system.domain.question.dto.QuestionQueryDTO;
import com.bite.system.domain.question.vo.QuestionVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IQuestionService {
    List<QuestionVO> list(QuestionQueryDTO questionQueryDTO);

    int add(QuestionAddDTO questionAddDTO);
}
