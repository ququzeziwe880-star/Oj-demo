package com.bite.friend.controller.exam;

import com.bite.common.core.controller.BaseController;
import com.bite.common.core.domain.TableDataInfo;
import com.bite.friend.domain.exam.dto.ExamQueryDTO;
import com.bite.friend.service.exam.IExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exam")
public class ExamController extends BaseController {

    @Autowired
    private IExamService examService;

    @GetMapping("/semiLogin/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        return getTableDataInfo(examService.list(examQueryDTO));
    }

    @GetMapping("/semiLogin/redis/list")
    public TableDataInfo redusList(ExamQueryDTO examQueryDTO) {
        return examService.redisList(examQueryDTO);
    }
}
