package com.bite.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysUserVO {
    @Schema(description = "用户账号")
    private String userName;

    @Schema(description = "用户昵称")
    private String nick;
}
