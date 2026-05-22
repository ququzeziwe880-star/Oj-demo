package com.bite.system.controller.user;

import com.bite.common.core.controller.BaseController;
import com.bite.common.core.domain.R;
import com.bite.common.core.domain.TableDataInfo;
import com.bite.system.domain.user.dto.UserDTO;
import com.bite.system.domain.user.dto.UserQueryDTO;
import com.bite.system.service.user.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "C端用户api")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @GetMapping("/list")
    public TableDataInfo list(UserQueryDTO userQueryDTO){
        return getTableDataInfo(userService.list(userQueryDTO));
    }
    @PutMapping("/updateStatus")
    public R<Void> status(@RequestBody UserDTO userDTO){
        return toR(userService.updateStatus(userDTO));
    }
}
