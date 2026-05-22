package com.bite.friend.controller;

import com.bite.common.core.controller.BaseController;
import com.bite.common.core.domain.R;
import com.bite.friend.domain.dto.UserDTO;
import com.bite.friend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @PostMapping("/sendCode")
    public R<Void> sendCode(@RequestBody UserDTO userDTO){
        return toR(userService.sendCode(userDTO));
    }

    @PostMapping("/code/login")
    public R<String> login(@RequestBody UserDTO userDTO){
        return R.ok(userService.codeLogin(userDTO.getPhone(),userDTO.getCode()));
    }

}
