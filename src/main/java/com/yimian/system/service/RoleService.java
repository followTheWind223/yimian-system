package com.yimian.system.service;

import com.yimian.system.dto.RoleCreateDto;
import com.yimian.system.dto.RolePermissionAssignDto;
import com.yimian.system.dto.RoleUpdateDto;
import com.yimian.system.entity.Role;
import java.util.List;

public interface RoleService {

    List<Role> list();

    Role getById(Long id);

    Role create(RoleCreateDto dto);

    Role update(Long id, RoleUpdateDto dto);

    void delete(Long id);

    Role setEnabled(Long id, boolean enabled);

    void assignPermissions(Long roleId, RolePermissionAssignDto dto);

    List<String> getPermCodes(Long roleId);
}
