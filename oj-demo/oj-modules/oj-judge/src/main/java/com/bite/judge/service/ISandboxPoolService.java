package com.bite.judge.service;

import com.bite.judge.domain.SandBoxExecuteResult;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ISandboxPoolService {
    SandBoxExecuteResult exeJavaCode(Long userId, String userCode, List<String> inputList);
}