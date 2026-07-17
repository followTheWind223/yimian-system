package com.yimian.system.security;

import com.yimian.system.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JWT UserDetails — 权限驱动模型
 * ADMIN 角色自动拥有全部权限，其他角色按 DB 关联查询
 */
@Data
public class JwtUserDetails implements UserDetails {

    private User user;
    private List<String> roles;
    private List<String> permissions;

    public JwtUserDetails(User user, List<String> roles, List<String> permissions) {
        this.user = user;
        this.roles = roles;
        this.permissions = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 角色 → hasRole('ADMIN') / hasAuthority('ROLE_ADMIN')
        if (roles != null) {
            roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }

        // 权限 → hasAuthority('user:create')
        if (permissions != null) {
            permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() == null || user.getStatus() != 2;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == null || user.getStatus() == 1;
    }
}
