package com.bite.system.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bite.system.test.domain.TestDomain;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper extends BaseMapper<TestDomain> {

}
