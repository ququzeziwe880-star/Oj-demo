package com.bite.common.core.enums;

import lombok.Getter;

@Getter
public enum UserIdentity {

    ORDINARY(1,"普通用户"),
    ADMIN(2,"管理员");


    private Integer value;

    private String desc;

    UserIdentity(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
