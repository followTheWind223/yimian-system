package com.yimian.system.security.service;

import com.yimian.system.entity.User;
import com.yimian.system.mapper.UserMapper;
import com.yimian.system.security.JwtUserDetails;
import com.yimian.system.security.SecurityConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security UserDetailsService 实现
 * 权限驱动模型：ADMIN 自动拥有全部权限，其他角色按关联表查询
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        return buildDetails(user);
    }

    /**
     * 根据用户 ID 加载
     */
    public UserDetails loadUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: id=" + userId);
        }
        return buildDetails(user);
    }

    // ==================== 私有方法 ====================

    private JwtUserDetails buildDetails(User user) {
        List<String> roles = userMapper.selectRoleCodesByUserId(user.getId());

        // ADMIN 直接持有全部权限（内存常量，不查 DB），其他角色按关联表查询
        List<String> permissions;
        if (roles.contains("ROLE_ADMIN")) {
            permissions = SecurityConstants.ALL_PERMISSIONS;
        } else {
            permissions = userMapper.selectPermCodesByUserId(user.getId());
        }

        return new JwtUserDetails(user, roles, permissions);
    }
}
