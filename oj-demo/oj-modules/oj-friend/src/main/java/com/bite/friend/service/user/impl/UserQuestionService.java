package com.bite.friend.service.user.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.bite.api.RemoteJudgeService;
import com.bite.api.domain.UserExeResult;
import com.bite.api.domain.dto.JudgeSubmitDTO;
import com.bite.api.domain.vo.UserQuestionResultVO;
import com.bite.common.core.constans.Constants;
import com.bite.common.core.domain.R;
import com.bite.common.core.enums.ProgramType;
import com.bite.common.core.enums.QuestionResult;
import com.bite.common.core.enums.ResultCode;
import com.bite.common.core.utils.ThreadLocalUtil;
import com.bite.common.security.exception.ServiceException;
import com.bite.friend.domain.question.Question;
import com.bite.friend.domain.question.QuestionCase;
import com.bite.friend.domain.question.es.QuestionES;
import com.bite.friend.domain.user.UserSubmit;
import com.bite.friend.domain.user.dto.UserSubmitDTO;
import com.bite.friend.elasticsearch.QuestionRepository;
import com.bite.friend.mapper.question.QuestionMapper;
import com.bite.friend.mapper.user.UserSubmitMapper;
import com.bite.friend.rabbit.JudgeProducer;
import com.bite.friend.service.user.IUserQuestionService;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class UserQuestionService implements IUserQuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Autowired
    private RemoteJudgeService remoteJudgeService;

    @Autowired
    private JudgeProducer judgeProducer;
    @Override
    public R<UserQuestionResultVO> submit(UserSubmitDTO submitDTO) {
        Integer programType = submitDTO.getProgramType();
        if (ProgramType.JAVA.getValue().equals(programType)){
            JudgeSubmitDTO judgeSubmitDTO = assembleJudgeSubmitDTO(submitDTO);
            return remoteJudgeService.doJudgeJavaCode(judgeSubmitDTO);
        }
        throw new ServiceException(ResultCode.FAILED_SUPPORT_PROGRAM);
    }

    @Override
    public boolean rabbitSubmit(UserSubmitDTO submitDTO) {
        Integer programType = submitDTO.getProgramType();
        if (ProgramType.JAVA.getValue().equals(programType)){
            JudgeSubmitDTO judgeSubmitDTO = assembleJudgeSubmitDTO(submitDTO);
            judgeProducer.produceMsg(judgeSubmitDTO);
            return true;
        }
        throw new ServiceException(ResultCode.FAILED_SUPPORT_PROGRAM);
    }

    @Override
    public UserQuestionResultVO exeResult(Long examId, Long questionId, String currentTime) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        UserSubmit userSubmit = userSubmitMapper.selectCurrentUserSubmit(userId,examId,questionId,currentTime);
        UserQuestionResultVO resultVO = new UserQuestionResultVO();
        if (userSubmit == null){
            resultVO.setPass(QuestionResult.ERROR.getValue());
        }else{
            resultVO.setPass(userSubmit.getPass());
            resultVO.setExeMessage(userSubmit.getExeMessage());
            if (StringUtil.isNotEmpty(userSubmit.getCaseJudgeRes())){
                resultVO.setUserExeResultList(JSON.parseArray(userSubmit.getCaseJudgeRes(), UserExeResult.class));
            }
        }
        return resultVO;
    }

    private JudgeSubmitDTO assembleJudgeSubmitDTO(UserSubmitDTO submitDTO) {
        Long questionId = submitDTO.getQuestionId();
        QuestionES questionES = questionRepository.findById(questionId).orElse(null);
        JudgeSubmitDTO judgeSubmitDTO = new JudgeSubmitDTO();
        if (questionES != null){
            BeanUtil.copyProperties(questionES,judgeSubmitDTO);
        }else{
            Question question = questionMapper.selectById(questionId);
            BeanUtil.copyProperties(question,judgeSubmitDTO);
            questionES = new QuestionES();
            BeanUtil.copyProperties(question,questionES);
            questionRepository.save(questionES);
        }
        judgeSubmitDTO.setUserId(ThreadLocalUtil.get(Constants.USER_ID,Long.class));
        judgeSubmitDTO.setExamId(submitDTO.getExamId());
        judgeSubmitDTO.setProgramType(submitDTO.getProgramType());
        judgeSubmitDTO.setUserCode(codeConnect(submitDTO.getUserCode(),questionES.getMainFuc()));
        List<QuestionCase> questionCaseList = JSONUtil.toList(questionES.getQuestionCase(), QuestionCase.class);
        List<String> inputList = questionCaseList.stream().map(QuestionCase::getInput).toList();
        judgeSubmitDTO.setInputList(inputList);
        List<String> outPutList = questionCaseList.stream().map(QuestionCase::getOutput).toList();
        judgeSubmitDTO.setOutputList(outPutList);
        return judgeSubmitDTO;
    }

    private String codeConnect(String userCode, String mainFunc) {
        String targetCharacter = "}";
        int targetLastIndex = userCode.lastIndexOf(targetCharacter);
        if (targetLastIndex != -1) {
            return userCode.substring(0, targetLastIndex) + "\n" + mainFunc + "\n" + userCode.substring(targetLastIndex);
        }
        throw new ServiceException(ResultCode.FAILED);
    }
}
