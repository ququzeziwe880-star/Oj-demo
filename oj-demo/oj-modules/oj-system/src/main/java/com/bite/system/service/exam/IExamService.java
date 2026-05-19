package com.bite.system.service.exam;

import com.bite.system.domain.exam.dto.ExamAddDTO;
import com.bite.system.domain.exam.dto.ExamEditDTO;
import com.bite.system.domain.exam.dto.ExamQueryDTO;
import com.bite.system.domain.exam.dto.ExamQuestionAddDTO;
import com.bite.system.domain.exam.vo.ExamDetailVO;
import com.bite.system.domain.exam.vo.ExamVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public interface IExamService {
    List<ExamVO> list(ExamQueryDTO examQueryDTO);

    String add(ExamAddDTO examAddDTO);

    boolean questionAdd(ExamQuestionAddDTO examQuestionAddDTO);

    int questionDelete(Long examId, Long questionId);

    ExamDetailVO detail(Long examId);

    int edit(ExamEditDTO examEditDTO);

    int delete(Long examId);

    int publish(Long examId);

    int cancelPublish(Long examId);
}

