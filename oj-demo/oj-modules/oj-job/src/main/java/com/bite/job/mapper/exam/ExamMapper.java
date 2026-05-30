package com.bite.job.mapper.exam;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bite.job.domain.exam.Exam;
import com.bite.job.domain.exam.dto.ExamQueryDTO;
import com.bite.job.domain.exam.vo.ExamVO;

import java.util.List;

public interface ExamMapper extends BaseMapper<Exam> {
    List<ExamVO> selectExamList(ExamQueryDTO examQueryDTO);

}
