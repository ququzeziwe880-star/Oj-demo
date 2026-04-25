package com.bite.system.domain;

import lombok.Data;
import lombok.Setter;

@Data
public class LoginUser {

    private Integer identity; // 1 表示普通用户 2 表示 管理员用户

}
