package com.bite.friend.controller.user;

import com.bite.common.core.constans.HttpConstants;
import com.bite.common.core.controller.BaseController;
import com.bite.common.core.domain.R;
import com.bite.common.core.domain.vo.LoginUserVO;
import com.bite.friend.domain.user.dto.UserDTO;
import com.bite.friend.domain.user.dto.UserUpdateDTO;
import com.bite.friend.domain.user.vo.UserVO;
import com.bite.friend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/logout")
    public R<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token){
        return toR(userService.logout(token));
    }

    @GetMapping("/info")
    public R<LoginUserVO> info(@RequestHeader(HttpConstants.AUTHENTICATION) String token){
        return userService.info(token);
    }

    @GetMapping("/detail")
    public R<UserVO> detail(){
        return R.ok(userService.detail());
    }

    @PutMapping("/edit")
    public R<Void> edit(@RequestBody UserUpdateDTO userUpdateDTO){
        return toR(userService.edit(userUpdateDTO));
    }

    @PutMapping("/head-image/update")
    public R<Void> updateHeadImage(@RequestBody UserUpdateDTO userUpdateDTO){
        return toR(userService.updateHeadImage(userUpdateDTO.getHeadImage()));
    }
}
