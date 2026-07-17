package com.yimian.system.service.impl;

import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.dto.RoleCreateDto;
import com.yimian.system.dto.RolePermissionAssignDto;
import com.yimian.system.dto.RoleUpdateDto;
import com.yimian.system.entity.Role;
import com.yimian.system.mapper.RoleMapper;
import com.yimian.system.service.RoleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public List<Role> list() {
        return roleMapper.selectAllIncludingDeleted();
    }

    @Override
    public Role getById(Long id) {
        Role role = roleMapper.selectByIdIncludingDeleted(id);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }
        return role;
    }

    @Override
    @Transactional
    public Role create(RoleCreateDto dto) {
        Role exist = roleMapper.selectAnyByRoleCode(dto.getRoleCode());
        if (exist != null) {
            throw new BusinessException(ResultCode.ROLE_CODE_EXISTS);
        }
        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        role.setDeleted(0);
        roleMapper.insertAuto(role);
        log.info("Role created: {}", role.getRoleCode());
        return role;
    }

    @Override
    @Transactional
    public Role update(Long id, RoleUpdateDto dto) {
        Role role = getById(id);
        if (dto.getRoleName() != null) role.setRoleName(dto.getRoleName());
        if (dto.getDescription() != null) role.setDescription(dto.getDescription());
        if (dto.getSort() != null) role.setSort(dto.getSort());
        roleMapper.updateBasicIncludingDeleted(role);
        log.info("Role updated: id={}", id);
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        setEnabled(id, false);
    }

    @Override
    @Transactional
    public Role setEnabled(Long id, boolean enabled) {
        getById(id);
        roleMapper.updateDeletedStatus(id, enabled ? 0 : 1);
        log.info("Role {}: id={}", enabled ? "enabled" : "disabled", id);
        return getById(id);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, RolePermissionAssignDto dto) {
        getById(roleId);
        roleMapper.deleteRolePermissions(roleId);
        if (!dto.getPermCodes().isEmpty()) {
            roleMapper.insertRolePermissions(roleId, dto.getPermCodes());
        }
        log.info("Role permissions assigned: roleId={}, perms={}", roleId, dto.getPermCodes());
    }

    @Override
    public List<String> getPermCodes(Long roleId) {
        getById(roleId);
        return roleMapper.selectPermCodesByRoleId(roleId);
    }
}
