package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.Role;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RoleMapper extends BaseMapper<Role> {

    List<Role> selectAllIncludingDeleted();

    Role selectByIdIncludingDeleted(@Param("id") Long id);

    Role selectByRoleCode(@Param("roleCode") String roleCode);

    Role selectAnyByRoleCode(@Param("roleCode") String roleCode);

    int insertAuto(Role role);

    int updateBasicIncludingDeleted(Role role);

    int updateDeletedStatus(@Param("id") Long id, @Param("deleted") Integer deleted);

    List<String> selectPermCodesByRoleId(@Param("roleId") Long roleId);

    int deleteRolePermissions(@Param("roleId") Long roleId);

    int insertRolePermissions(@Param("roleId") Long roleId, @Param("permCodes") List<String> permCodes);
}
