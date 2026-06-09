package com.bite.friend.controller.user;

import com.bite.api.domain.vo.UserQuestionResultVO;
import com.bite.common.core.controller.BaseController;
import com.bite.common.core.domain.R;
import com.bite.friend.domain.user.dto.UserSubmitDTO;
import com.bite.friend.service.user.IUserQuestionService;
import com.bite.friend.service.user.impl.UserQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/question")
public class UserQuestionController extends BaseController{

    @Autowired
    private IUserQuestionService userQuestionService;

    @PostMapping("/submit")
    public R<UserQuestionResultVO> submit(@RequestBody UserSubmitDTO submitDTO){
        return userQuestionService.submit(submitDTO);
    }

    @PostMapping("/rabbit/submit")
    public R<Void> rabbitSubmit(@RequestBody UserSubmitDTO submitDTO){
        return toR(userQuestionService.rabbitSubmit(submitDTO));
    }

    @GetMapping("/exe/result")
    public R<UserQuestionResultVO> exeResult(Long examId, Long questionId, String currentTime){
        return R.ok(userQuestionService.exeResult(examId,questionId,currentTime));
    }
}
