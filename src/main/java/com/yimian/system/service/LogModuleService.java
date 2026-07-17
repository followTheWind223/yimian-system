package com.yimian.system.service;

import com.yimian.system.dto.LogModuleCreateDto;
import com.yimian.system.dto.LogModuleUpdateDto;
import com.yimian.system.entity.LogModule;

import java.util.List;

public interface LogModuleService {

    List<LogModule> list();

    List<LogModule> listEnabled();

    LogModule getById(Long id);

    LogModule create(LogModuleCreateDto dto);

    LogModule update(Long id, LogModuleUpdateDto dto);

    void delete(Long id);
}
