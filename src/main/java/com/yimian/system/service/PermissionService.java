package com.yimian.system.service;

import com.yimian.system.dto.PermissionCreateDto;
import com.yimian.system.dto.PermissionUpdateDto;
import com.yimian.system.entity.Permission;
import java.util.List;

public interface PermissionService {

    List<Permission> list();

    Permission getById(Long id);

    Permission create(PermissionCreateDto dto);

    Permission update(Long id, PermissionUpdateDto dto);

    void delete(Long id);

    Permission setEnabled(Long id, boolean enabled);
}
