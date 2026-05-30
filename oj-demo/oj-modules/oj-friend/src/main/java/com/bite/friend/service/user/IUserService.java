package com.bite.friend.service.user;

import com.bite.common.core.domain.R;
import com.bite.common.core.domain.vo.LoginUserVO;
import com.bite.friend.domain.user.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service

public interface IUserService {
    boolean sendCode(UserDTO userDTO);

    String codeLogin(String phone, String code);

    boolean logout(String token);

    R<LoginUserVO> info(String token);
}
