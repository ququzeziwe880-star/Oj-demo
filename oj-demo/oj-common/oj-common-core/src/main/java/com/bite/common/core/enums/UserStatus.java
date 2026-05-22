package com.bite.common.core.enums;

import lombok.Data;
import net.sf.jsqlparser.statement.Block;

public enum UserStatus {

    Block(0),

    Normal(1),
    ;

    private Integer value;

    UserStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
