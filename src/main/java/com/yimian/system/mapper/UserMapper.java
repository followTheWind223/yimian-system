package com.yimian.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yimian.system.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper (MySQL)
 * 继承 MyBatis-Plus BaseMapper，自动拥有 CRUD 方法
 * 复杂查询在 resources/mapper/mysql/UserMapper.xml 中定义
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询
     */
    User selectByEmail(@Param("email") String email);

    /**
     * 查询用户列表（配合 PageHelper.startPage 实现分页）
     */
    List<User> selectUserPage(@Param("keyword") String keyword,
                              @Param("role") String role,
                              @Param("status") Integer status);

    /**
     * 查询用户的角色编码列表
     */
    java.util.List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的权限编码列表（通过角色关联）
     */
    java.util.List<String> selectPermCodesByUserId(@Param("userId") Long userId);

    /**
     * 为用户分配角色
     */
    int insertUserRole(@Param("userId") Long userId, @Param("roleCode") String roleCode);

    /**
     * 更新最后登录信息
     */
    int updateLoginInfo(@Param("id") Long id,
                        @Param("loginTime") java.time.LocalDateTime loginTime,
                        @Param("loginIp") String loginIp);
}
