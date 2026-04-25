package com.bite.common.core.controller;

import com.bite.common.core.domain.R;

public class BaseController {


    public R<Void> toR(int rows){
       return rows > 0 ? R.ok() : R.fail();
    }

    public boolean toR(boolean result){
        return result ? true : false;
    }
}
