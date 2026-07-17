package com.yimian.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.dto.*;
import com.yimian.system.entity.Role;
import com.yimian.system.entity.User;
import com.yimian.system.entity.UserFollow;
import com.yimian.system.mapper.RoleMapper;
import com.yimian.system.mapper.UserFollowMapper;
import com.yimian.system.mapper.UserMapper;
import com.yimian.system.mapper.UserRoleMapper;
import com.yimian.system.security.JwtUtil;
import com.yimian.system.service.UserService;
import com.yimian.system.vo.LoginVO;
import com.yimian.system.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserFollowMapper userFollowMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final boolean captchaEnabled;

    public UserServiceImpl(UserMapper userMapper,
                           UserFollowMapper userFollowMapper,
                           UserRoleMapper userRoleMapper,
                           RoleMapper roleMapper,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           @Value("${captcha.enabled}") boolean captchaEnabled) {
        this.userMapper = userMapper;
        this.userFollowMapper = userFollowMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.captchaEnabled = captchaEnabled;
    }

    // ==================== 认证 ====================

    @Override
    public LoginVO login(LoginDto dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        if (captchaEnabled) {
            // TODO: 验证码校验逻辑
        }
        List<String> roles = userMapper.selectRoleCodesByUserId(user.getId());
        List<String> permissions = getPermissions(user.getId(), roles);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roles", String.join(",", roles));
        claims.put("permissions", String.join(",", permissions));

        String accessToken = jwtUtil.generateToken(user.getUsername(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        userMapper.updateLoginInfo(user.getId(),
                java.time.LocalDateTime.now(), "127.0.0.1");

        log.info("用户登录成功: {}", user.getUsername());

        return LoginVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(7200L)
                .permissions(permissions)
                .userInfo(toVO(user))
                .build();
    }

    @Override
    public LoginVO adminLogin(LoginDto dto) {
        LoginVO vo = login(dto);
        if (vo.getUserInfo() == null || vo.getUserInfo().getRoles() == null
                || !vo.getUserInfo().getRoles().contains("ROLE_ADMIN")) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        log.info("管理员登录成功: {}", dto.getUsername());
        return vo;
    }

    @Override
    public LoginVO userLogin(LoginDto dto) {
        return login(dto);
    }

    @Override
    public UserVO register(RegisterDto dto) {
        User existUser = userMapper.selectByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            User existEmail = userMapper.selectByEmail(dto.getEmail());
            if (existEmail != null) {
                throw new BusinessException(ResultCode.EMAIL_EXISTS);
            }
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setStatus(1);
        user.setDeleted(0);

        try {
            userMapper.insert(user);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("uk_username")) {
                throw new BusinessException(ResultCode.USERNAME_EXISTS);
            }
            if (msg != null && msg.contains("uk_email")) {
                throw new BusinessException(ResultCode.EMAIL_EXISTS);
            }
            throw new BusinessException(ResultCode.ERROR, "数据重复");
        }

        userMapper.insertUserRole(user.getId(), "ROLE_USER");
        log.info("用户注册成功: {} (id={})", user.getUsername(), user.getId());
        return toVO(user);
    }

    // ==================== 管理员：用户 CRUD ====================

    @Override
    public PageInfo<UserVO> listUsers(int page, int size, String keyword, String role, Integer status) {
        PageHelper.startPage(page, size);
        List<User> users = userMapper.selectUserPage(keyword, role, status);
        List<UserVO> voList = users.stream().map(this::toVO).collect(Collectors.toList());
        return new PageInfo<>(voList);
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return toVO(user);
    }

    @Override
    public UserVO getPublicUserById(Long id, Long currentUserId) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return toPublicVO(user, currentUserId);
    }

    @Override
    @Transactional
    public UserVO toggleFollow(Long followeeId, Long followerId) {
        if (followeeId == null || followerId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        if (followeeId.equals(followerId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不能关注自己");
        }
        User followee = userMapper.selectById(followeeId);
        if (followee == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        UserFollow active = userFollowMapper.selectActive(followerId, followeeId);
        if (active != null) {
            userFollowMapper.softDelete(followerId, followeeId);
        } else {
            UserFollow any = userFollowMapper.selectAny(followerId, followeeId);
            if (any != null) {
                userFollowMapper.restore(any.getId());
            } else {
                UserFollow follow = new UserFollow();
                follow.setFollowerId(followerId);
                follow.setFolloweeId(followeeId);
                follow.setDeleted(0);
                userFollowMapper.insert(follow);
            }
        }
        return toPublicVO(followee, followerId);
    }

    @Override
    public PageInfo<UserVO> listFollowing(Long id, Long currentUserId, int page, int size) {
        ensureUserExists(id);
        PageHelper.startPage(page, size);
        List<User> users = userFollowMapper.selectFollowingUsers(id);
        PageInfo<User> source = new PageInfo<>(users);
        List<UserVO> list = users.stream()
                .map(user -> toPublicVO(user, currentUserId))
                .collect(Collectors.toList());
        return copyUserPage(source, list);
    }

    @Override
    public PageInfo<UserVO> listFollowers(Long id, Long currentUserId, int page, int size) {
        ensureUserExists(id);
        PageHelper.startPage(page, size);
        List<User> users = userFollowMapper.selectFollowerUsers(id);
        PageInfo<User> source = new PageInfo<>(users);
        List<UserVO> list = users.stream()
                .map(user -> toPublicVO(user, currentUserId))
                .collect(Collectors.toList());
        return copyUserPage(source, list);
    }

    @Override
    @Transactional
    public UserVO createUser(UserCreateDto dto) {
        User existUser = userMapper.selectByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            User existEmail = userMapper.selectByEmail(dto.getEmail());
            if (existEmail != null) {
                throw new BusinessException(ResultCode.EMAIL_EXISTS);
            }
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setStatus(1);
        user.setDeleted(0);

        try {
            userMapper.insert(user);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("uk_username"))
                throw new BusinessException(ResultCode.USERNAME_EXISTS);
            if (msg != null && msg.contains("uk_email"))
                throw new BusinessException(ResultCode.EMAIL_EXISTS);
            throw new BusinessException(ResultCode.ERROR, "数据重复");
        }

        // 分配角色
        if (dto.getRoleCodes() != null && !dto.getRoleCodes().isEmpty()) {
            assignRoleCodes(user.getId(), dto.getRoleCodes());
        } else {
            userMapper.insertUserRole(user.getId(), "ROLE_USER");
        }

        log.info("管理员创建用户成功: {} (id={})", user.getUsername(), user.getId());
        return toVO(user);
    }

    @Override
    @Transactional
    public UserVO updateUser(Long id, UserUpdateDto dto) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        userMapper.updateById(user);
        log.info("管理员编辑用户成功: id={}", id);
        return toVO(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, AdminResetPasswordDto dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);
        log.info("管理员重置用户密码: userId={}", userId);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        userMapper.deleteById(id);
        log.info("管理员删除用户成功: id={}", id);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, UserRoleAssignDto dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        assignRoleCodes(userId, dto.getRoleCodes());
        log.info("用户角色分配成功: userId={}, roles={}", userId, dto.getRoleCodes());
    }

    // ==================== 个人中心 ====================

    @Override
    @Transactional
    public UserVO updateProfile(Long userId, ProfileUpdateDto dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 邮箱唯一校验
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()
                && !dto.getEmail().equals(user.getEmail())) {
            User existEmail = userMapper.selectByEmail(dto.getEmail());
            if (existEmail != null) {
                throw new BusinessException(ResultCode.EMAIL_EXISTS);
            }
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        userMapper.updateById(user);
        log.info("用户个人信息更新成功: userId={}", userId);
        return toVO(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordDto dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 验证旧密码
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);
        log.info("用户密码修改成功: userId={}", userId);
    }

    // ==================== 私有方法 ====================

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        List<String> roles = userMapper.selectRoleCodesByUserId(user.getId());
        vo.setRoles(roles);
        vo.setPermissions(getPermissions(user.getId(), roles));
        return vo;
    }

    private UserVO toPublicVO(User user, Long currentUserId) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setEmail(null);
        vo.setPhone(null);
        vo.setLastLoginTime(null);
        vo.setRoles(Collections.emptyList());
        vo.setPermissions(Collections.emptyList());
        fillFollowStats(vo, currentUserId);
        return vo;
    }

    private void fillFollowStats(UserVO vo, Long currentUserId) {
        vo.setFollowingCount(userFollowMapper.countFollowing(vo.getId()));
        vo.setFollowerCount(userFollowMapper.countFollowers(vo.getId()));
        vo.setFollowed(currentUserId != null
                && !currentUserId.equals(vo.getId())
                && userFollowMapper.selectActive(currentUserId, vo.getId()) != null);
    }

    /**
     * ADMIN 用内存常量（不查 DB），其他角色按关联表查询
     */
    private void ensureUserExists(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
    }

    private PageInfo<UserVO> copyUserPage(PageInfo<User> source, List<UserVO> list) {
        PageInfo<UserVO> target = new PageInfo<>();
        target.setList(list);
        target.setTotal(source.getTotal());
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setSize(source.getSize());
        target.setPages(source.getPages());
        target.setStartRow(source.getStartRow());
        target.setEndRow(source.getEndRow());
        target.setPrePage(source.getPrePage());
        target.setNextPage(source.getNextPage());
        target.setIsFirstPage(source.isIsFirstPage());
        target.setIsLastPage(source.isIsLastPage());
        target.setHasPreviousPage(source.isHasPreviousPage());
        target.setHasNextPage(source.isHasNextPage());
        target.setNavigatePages(source.getNavigatePages());
        target.setNavigatepageNums(source.getNavigatepageNums());
        target.setNavigateFirstPage(source.getNavigateFirstPage());
        target.setNavigateLastPage(source.getNavigateLastPage());
        return target;
    }

    private List<String> getPermissions(Long userId, List<String> roles) {
        if (roles.contains("ROLE_ADMIN")) {
            return com.yimian.system.security.SecurityConstants.ALL_PERMISSIONS;
        }
        return userMapper.selectPermCodesByUserId(userId);
    }

    private void assignRoleCodes(Long userId, List<String> roleCodes) {
        userRoleMapper.deleteByUserId(userId);
        if (roleCodes != null && !roleCodes.isEmpty()) {
            List<Long> roleIds = roleCodes.stream()
                    .map(code -> {
                        Role r = roleMapper.selectByRoleCode(code);
                        if (r == null) throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
                        return r.getId();
                    })
                    .collect(Collectors.toList());
            userRoleMapper.insertBatch(userId, roleIds);
        }
    }
}
