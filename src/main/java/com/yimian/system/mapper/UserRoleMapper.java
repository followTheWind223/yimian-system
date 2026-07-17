package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.UserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联 Mapper
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 删除用户的所有角色关联
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 批量插入用户角色关联
     */
    int insertBatch(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
}
