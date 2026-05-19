package com.bite.system.domain.exam.dto;

import com.bite.common.core.domain.PageQueryDTO;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ExamQueryDTO extends PageQueryDTO {

    private String title;

    private String startTime;

    private String endTime;
}
