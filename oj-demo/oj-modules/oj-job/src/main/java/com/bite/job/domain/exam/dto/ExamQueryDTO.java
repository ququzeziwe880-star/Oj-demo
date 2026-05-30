package com.bite.job.domain.exam.dto;

import com.bite.common.core.domain.PageQueryDTO;
import lombok.Data;

@Data
public class ExamQueryDTO extends PageQueryDTO {

    private String title;

    private String startTime;

    private String endTime;

    private Integer type; // 0 表示未完赛 1 表示已经完赛
}
