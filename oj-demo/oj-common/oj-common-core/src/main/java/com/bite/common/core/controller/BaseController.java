package com.bite.common.core.controller;

import com.bite.common.core.domain.R;
import com.bite.common.core.domain.TableDataInfo;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.lang.Collections;

import java.util.List;

public class BaseController {


    public R<Void> toR(int rows){
       return rows > 0 ? R.ok() : R.fail();
    }

    public R<Void> toR(boolean result){
        return result ? R.ok() : R.fail();
    }

    public TableDataInfo getTableDataInfo(List<?> list){
        if (Collections.isEmpty(list)){
            return TableDataInfo.empty();
        }
        //new PageInfo<>(list).getTotal();

        //return TableDataInfo.success(list,list.size());
        return TableDataInfo.success(list,new PageInfo<>(list).getTotal());
    }
}
