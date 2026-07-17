package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.LogModule;
import org.apache.ibatis.annotations.Param;

/**
 * 日志模块字典 Mapper
 */
public interface LogModuleMapper extends BaseMapper<LogModule> {

    LogModule selectByCode(@Param("code") String code);
}
