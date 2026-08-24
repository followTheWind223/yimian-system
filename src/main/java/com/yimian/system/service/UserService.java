package com.yimian.system.service;

import com.github.pagehelper.PageInfo;
import com.yimian.system.dto.*;
import com.yimian.system.vo.LoginVO;
import com.yimian.system.vo.UserVO;

import java.util.List;

public interface UserService {

    LoginVO login(LoginDto dto);

    LoginVO adminLogin(LoginDto dto);

    LoginVO userLogin(LoginDto dto);

    UserVO register(RegisterDto dto);

    void forgotPasswordReset(ForgotPasswordResetDto dto);

    PageInfo<UserVO> listUsers(int page, int size, String keyword, String role, Integer status);

    UserVO getUserById(Long id);

    UserVO getPublicUserById(Long id, Long currentUserId);

    UserVO toggleFollow(Long followeeId, Long followerId);

    PageInfo<UserVO> listFollowing(Long id, Long currentUserId, int page, int size);

    PageInfo<UserVO> listFollowers(Long id, Long currentUserId, int page, int size);

    List<UserVO> listMentionableFriends(Long currentUserId, String keyword, Integer limit);

    UserVO createUser(UserCreateDto dto);

    UserVO updateUser(Long id, UserUpdateDto dto);

    void resetPassword(Long userId, AdminResetPasswordDto dto);

    void deleteUser(Long id);

    void assignRoles(Long userId, UserRoleAssignDto dto);

    UserVO updateProfile(Long userId, ProfileUpdateDto dto);

    void changePassword(Long userId, ChangePasswordDto dto);
}
