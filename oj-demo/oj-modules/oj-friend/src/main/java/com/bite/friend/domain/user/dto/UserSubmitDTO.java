package com.bite.friend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSubmitDTO {

    private Long examId;

    private Long questionId;

    private Integer programType; // 1.java 2,c++ 3.go

    private String userCode;


}
