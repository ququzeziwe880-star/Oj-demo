package com.bite.common.security.exception;

import com.bite.common.core.enums.ResultCode;
import lombok.Data;

import javax.xml.transform.Result;
@Data
public class ServiceException extends RuntimeException{

    private ResultCode resultCode;

    public ServiceException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

}
