package com.bite.system.controller.question;

import com.bite.common.core.controller.BaseController;
import com.bite.common.core.domain.R;
import com.bite.common.core.domain.TableDataInfo;
import com.bite.system.domain.question.dto.QuestionAddDTO;
import com.bite.system.domain.question.dto.QuestionQueryDTO;
import com.bite.system.domain.question.vo.QuestionVO;
import com.bite.system.service.question.IQuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
@Tag(name = "题目管理接口")
public class QuestionController extends BaseController {

    @Autowired
    private IQuestionService questionService;


    @GetMapping("/list")
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO){
        List<QuestionVO> list = questionService.list(questionQueryDTO);
        return getTableDataInfo(list);
    }

    @PostMapping("/add")
    public R<Void> add(@RequestBody QuestionAddDTO questionAddDTO){
       return toR(questionService.add(questionAddDTO));
    }
}
