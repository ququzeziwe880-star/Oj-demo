package com.bite.common.core.domain;

import com.bite.common.core.enums.ResultCode;
import lombok.Data;

@Data
public class R<T> {

    private int code;

    private String msg;

    private T data;

    public static <T> R<T> ok(){
        return assenmbleResult(null,ResultCode.SUCCESS);
    }

    public static <T> R<T> ok(T data){
        return assenmbleResult(data,ResultCode.SUCCESS);
    }

    public static <T> R<T> fail(){
        return assenmbleResult(null,ResultCode.FAILED);
    }

    public static <T> R<T> fail(ResultCode resultCode){
        return assenmbleResult(null,resultCode);
    }

    private static <T> R<T> assenmbleResult(T data, ResultCode resultCode){
        R<T> r = new R<>();
        r.setCode(resultCode.getCode());
        r.setData(data);
        r.setMsg(resultCode.getMsg());
        return r;
    }
}
