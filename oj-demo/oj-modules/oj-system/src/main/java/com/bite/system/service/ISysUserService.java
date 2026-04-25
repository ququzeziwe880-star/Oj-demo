package com.bite.system.service;

import com.bite.common.core.domain.R;
import com.bite.system.domain.dto.SysUserSaveDTO;
import org.springframework.stereotype.Service;

@Service
public interface ISysUserService {
    R<String> login(String userAccount, String password);

    int add(SysUserSaveDTO sysUserSaveDTO);
}
