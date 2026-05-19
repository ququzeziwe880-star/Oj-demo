package com.bite.system.domain.exam.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class ExamQuestionAddDTO {

    private Long examId;

    private LinkedHashSet<Long> questionIdSet;
}
