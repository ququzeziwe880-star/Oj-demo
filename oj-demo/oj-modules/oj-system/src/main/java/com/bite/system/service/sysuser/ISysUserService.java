package com.bite.system.service.sysuser;

import com.bite.common.core.domain.R;
import com.bite.common.core.domain.vo.LoginUserVO;
import com.bite.system.domain.sysuser.dto.SysUserSaveDTO;
import org.springframework.stereotype.Service;

@Service
public interface ISysUserService {
    R<String> login(String userAccount, String password);

    boolean logout(String token);

    int add(SysUserSaveDTO sysUserSaveDTO);

    R<LoginUserVO> info(String token);

}
