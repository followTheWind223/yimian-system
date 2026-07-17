package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.Permission;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PermissionMapper extends BaseMapper<Permission> {

    List<Permission> selectAllIncludingDeleted();

    Permission selectByIdIncludingDeleted(@Param("id") Long id);

    Permission selectByPermCode(@Param("permCode") String permCode);

    Permission selectAnyByPermCode(@Param("permCode") String permCode);

    int updateBasicIncludingDeleted(Permission permission);

    int updateDeletedStatus(@Param("id") Long id, @Param("deleted") Integer deleted);

    List<String> selectAllPermCodes();
}
