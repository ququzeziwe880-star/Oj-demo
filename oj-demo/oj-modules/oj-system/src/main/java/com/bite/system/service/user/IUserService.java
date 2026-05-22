package com.bite.system.service.user;

import com.bite.system.domain.sysuser.vo.SysUserVO;
import com.bite.system.domain.user.dto.UserDTO;
import com.bite.system.domain.user.dto.UserQueryDTO;
import com.bite.system.domain.user.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public interface IUserService {
    List<UserVO> list(UserQueryDTO userQueryDTO);

    int updateStatus(UserDTO userDTO);
}
