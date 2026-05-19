package com.bite.system.domain.question.dto;

import com.bite.common.core.domain.PageQueryDTO;
import lombok.Data;

import java.util.Set;

@Data
public class QuestionQueryDTO extends PageQueryDTO {

    private String title;

    private Integer difficulty;

    private String excludeIdStr;

    private Set<Long> excludeIdSet;
}
