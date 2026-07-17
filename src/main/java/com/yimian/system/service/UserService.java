package com.yimian.system.service;

import com.github.pagehelper.PageInfo;
import com.yimian.system.dto.*;
import com.yimian.system.vo.LoginVO;
import com.yimian.system.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /** 用户登录（通用，允许所有角色） */
    LoginVO login(LoginDto dto);

    /** 管理员登录（校验 ROLE_ADMIN，无则拒绝） */
    LoginVO adminLogin(LoginDto dto);

    /** 用户端登录（语义独立，等价于通用 login，允许所有角色） */
    LoginVO userLogin(LoginDto dto);

    /** 用户注册 */
    UserVO register(RegisterDto dto);

    /** 分页查询用户列表（管理员） */
    PageInfo<UserVO> listUsers(int page, int size, String keyword, String role, Integer status);

    /** 用户详情（管理员） */
    UserVO getUserById(Long id);

    UserVO getPublicUserById(Long id, Long currentUserId);

    UserVO toggleFollow(Long followeeId, Long followerId);

    PageInfo<UserVO> listFollowing(Long id, Long currentUserId, int page, int size);

    PageInfo<UserVO> listFollowers(Long id, Long currentUserId, int page, int size);

    /** 新增用户（管理员） */
    UserVO createUser(UserCreateDto dto);

    /** 编辑用户（管理员） */
    UserVO updateUser(Long id, UserUpdateDto dto);

    /** 重置用户密码（管理员） */
    void resetPassword(Long userId, AdminResetPasswordDto dto);

    /** 删除用户（管理员） */
    void deleteUser(Long id);

    /** 分配角色（管理员） */
    void assignRoles(Long userId, UserRoleAssignDto dto);

    /** 修改个人信息（当前用户） */
    UserVO updateProfile(Long userId, ProfileUpdateDto dto);

    /** 修改密码（当前用户） */
    void changePassword(Long userId, ChangePasswordDto dto);
}
