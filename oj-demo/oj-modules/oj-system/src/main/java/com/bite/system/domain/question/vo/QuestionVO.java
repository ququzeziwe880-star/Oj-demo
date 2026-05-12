package com.bite.system.domain.question.vo;

import com.bite.common.core.domain.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class QuestionVO {

    private Long questionId;

    private String title;

    private Integer difficulty;

    private String createName;

    private LocalDateTime createTime;
}
