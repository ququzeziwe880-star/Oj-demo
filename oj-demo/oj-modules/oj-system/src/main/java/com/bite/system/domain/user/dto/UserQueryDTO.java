package com.bite.system.domain.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bite.common.core.domain.PageQueryDTO;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class UserQueryDTO extends PageQueryDTO {

    private Long userId;

    private String nickName;
}
