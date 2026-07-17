package com.yimian.system.service.impl;

import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.dto.PermissionCreateDto;
import com.yimian.system.dto.PermissionUpdateDto;
import com.yimian.system.entity.Permission;
import com.yimian.system.mapper.PermissionMapper;
import com.yimian.system.service.PermissionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public List<Permission> list() {
        return permissionMapper.selectAllIncludingDeleted();
    }

    @Override
    public Permission getById(Long id) {
        Permission perm = permissionMapper.selectByIdIncludingDeleted(id);
        if (perm == null) {
            throw new BusinessException(ResultCode.PERM_NOT_FOUND);
        }
        return perm;
    }

    @Override
    @Transactional
    public Permission create(PermissionCreateDto dto) {
        Permission exist = permissionMapper.selectAnyByPermCode(dto.getPermCode());
        if (exist != null) {
            throw new BusinessException(ResultCode.PERM_CODE_EXISTS);
        }
        Permission perm = new Permission();
        BeanUtils.copyProperties(dto, perm);
        perm.setDeleted(0);
        permissionMapper.insert(perm);
        log.info("Permission created: {}", perm.getPermCode());
        return perm;
    }

    @Override
    @Transactional
    public Permission update(Long id, PermissionUpdateDto dto) {
        Permission perm = getById(id);
        if (dto.getPermName() != null) perm.setPermName(dto.getPermName());
        if (dto.getDescription() != null) perm.setDescription(dto.getDescription());
        permissionMapper.updateBasicIncludingDeleted(perm);
        log.info("Permission updated: id={}", id);
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        setEnabled(id, false);
    }

    @Override
    @Transactional
    public Permission setEnabled(Long id, boolean enabled) {
        getById(id);
        permissionMapper.updateDeletedStatus(id, enabled ? 0 : 1);
        log.info("Permission {}: id={}", enabled ? "enabled" : "disabled", id);
        return getById(id);
    }
}
