package com.bite.friend.controller.user;

import com.bite.common.core.constans.HttpConstants;
import com.bite.common.core.controller.BaseController;
import com.bite.common.core.domain.R;
import com.bite.common.core.domain.TableDataInfo;
import com.bite.friend.componet.CheckUserStatus;
import com.bite.friend.domain.exam.dto.ExamDTO;
import com.bite.friend.domain.exam.dto.ExamQueryDTO;
import com.bite.friend.service.exam.IUserExamService;
import com.bite.friend.service.exam.impl.UserExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/exam")
public class UserExamController extends BaseController {

    @Autowired
    private IUserExamService userExamService;

    @CheckUserStatus
    @PostMapping("/enter")
    public R<Void> enter(@RequestHeader(HttpConstants.AUTHENTICATION) String token, @RequestBody ExamDTO examDTO){
        return toR(userExamService.enter(token,examDTO.getExamId()));
    }

    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        return userExamService.list(examQueryDTO);
    }
}
