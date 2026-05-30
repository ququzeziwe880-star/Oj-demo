package com.bite.friend.service.exam;

import com.bite.common.core.domain.TableDataInfo;
import com.bite.friend.domain.exam.dto.ExamQueryDTO;
import com.bite.friend.domain.exam.vo.ExamVO;
import org.springframework.stereotype.Service;

import java.util.List;


@Service

public interface IExamService {
    List<ExamVO> list(ExamQueryDTO examQueryDTO);

    TableDataInfo redisList(ExamQueryDTO examQueryDTO);
}

