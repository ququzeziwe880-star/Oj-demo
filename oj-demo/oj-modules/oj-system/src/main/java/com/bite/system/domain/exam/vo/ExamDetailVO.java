package com.bite.system.domain.exam.vo;

import com.bite.common.core.domain.BaseEntity;
import com.bite.system.domain.exam.ExamQuestion;
import com.bite.system.domain.question.vo.QuestionVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamDetailVO {

    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private List<QuestionVO> examQuestionList;

}
