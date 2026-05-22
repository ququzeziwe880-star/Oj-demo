package com.bite.friend.service;

import com.bite.friend.domain.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service

public interface IUserService {
    boolean sendCode(UserDTO userDTO);

    String codeLogin(String phone, String code);
}
