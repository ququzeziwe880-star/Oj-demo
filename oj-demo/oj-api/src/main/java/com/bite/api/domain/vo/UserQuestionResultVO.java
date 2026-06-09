package com.bite.api.domain.vo;

import com.bite.api.domain.UserExeResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserQuestionResultVO {

    private Integer pass; // 0. 未通过 1.通过

    private String exeMessage;

    private List<UserExeResult> userExeResultList;

    @JsonIgnore
    private Integer score;

}
