package com.bite.friend.service.question;

import com.bite.common.core.domain.TableDataInfo;
import com.bite.friend.domain.question.dto.QuestionQueryDTO;
import org.springframework.stereotype.Service;

@Service
public interface IQuestionService {

    TableDataInfo list(QuestionQueryDTO questionQueryDTO);
}
