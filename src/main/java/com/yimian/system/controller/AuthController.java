package com.yimian.system.controller;

import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.LoginDto;
import com.yimian.system.dto.RegisterDto;
import com.yimian.system.service.UserService;
import com.yimian.system.vo.LoginVO;
import com.yimian.system.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户登录与注册")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @OperationLog(module = "AUTH", operation = "用户登录", description = "用户 #{#dto.username} 尝试登录")
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDto dto) {
        return Result.success(userService.login(dto));
    }

    @OperationLog(module = "AUTH", operation = "管理员登录", description = "管理员 #{#dto.username} 登录后台")
    @Operation(summary = "管理员登录（校验 ROLE_ADMIN）")
    @PostMapping("/admin/login")
    public Result<LoginVO> adminLogin(@Valid @RequestBody LoginDto dto) {
        return Result.success(userService.adminLogin(dto));
    }

    @OperationLog(module = "AUTH", operation = "用户端登录", description = "用户 #{#dto.username} 登录用户端")
    @Operation(summary = "用户端登录")
    @PostMapping("/user/login")
    public Result<LoginVO> userLogin(@Valid @RequestBody LoginDto dto) {
        return Result.success(userService.userLogin(dto));
    }

    @OperationLog(module = "AUTH", operation = "用户注册", description = "新用户注册 #{#dto.username}")
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterDto dto) {
        return Result.success(userService.register(dto));
    }
}
