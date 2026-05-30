package com.bite.friend.service.exam;

import com.bite.common.core.domain.TableDataInfo;
import com.bite.friend.domain.exam.dto.ExamDTO;
import com.bite.friend.domain.exam.dto.ExamQueryDTO;
import org.springframework.stereotype.Service;

@Service

public interface IUserExamService {

    int enter(String token, Long examId);

    TableDataInfo list(ExamQueryDTO examQueryDTO);
}
