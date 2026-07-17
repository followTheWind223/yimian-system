package com.yimian.system.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /** 成功 */
    SUCCESS(200, "操作成功"),

    /** 客户端错误 */
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或 Token 已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    /** 服务端错误 */
    ERROR(500, "服务器内部错误"),

    /** 业务异常 */
    USERNAME_EXISTS(1001, "用户名已存在"),
    EMAIL_EXISTS(1002, "邮箱已被注册"),
    USER_NOT_FOUND(1003, "用户不存在"),
    PASSWORD_ERROR(1004, "密码错误"),
    ACCOUNT_DISABLED(1005, "账号已被禁用"),
    TOKEN_EXPIRED(1007, "Token 已过期"),
    ROLE_CODE_EXISTS(1010, "角色编码已存在"),
    ROLE_NOT_FOUND(1011, "角色不存在"),
    ROLE_IN_USE(1012, "角色已被分配，无法删除"),
    PERM_CODE_EXISTS(1020, "权限编码已存在"),
    PERM_NOT_FOUND(1021, "权限不存在"),
    LOG_MODULE_CODE_EXISTS(1030, "模块编码已存在"),

    /** 文件上传 */
    FILE_EMPTY(1100, "上传文件不能为空"),
    FILE_SAVE_FAILED(1101, "文件保存失败"),
    FILE_NOT_FOUND(1102, "文件不存在"),
    FILE_ILLEGAL_PATH(1103, "非法的文件路径"),
    FILE_READ_FAILED(1104, "文件读取失败"),

    /** 知识模块 */
    KNOWLEDGE_CONTENT_DUPLICATE(1201, "该内容已存在，请勿重复上传"),
    KNOWLEDGE_NOT_FOUND(1202, "知识题目不存在"),
    KNOWLEDGE_STATUS_ERROR(1203, "题目状态不允许此操作"),

    /** 标签模块 */
    TAG_NOT_FOUND(1301, "标签不存在"),
    TAG_NAME_EXISTS(1302, "标签名称已存在"),
    /** Favorite module */
    FAVORITE_FOLDER_NOT_FOUND(1401, "收藏夹不存在"),
    FAVORITE_FORBIDDEN(1402, "无权操作该收藏夹"),
    FAVORITE_ITEM_EXISTS(1403, "该内容已收藏到此收藏夹"),
    FAVORITE_ITEM_NOT_FOUND(1404, "收藏条目不存在"),
    FAVORITE_FOLDER_PRIVATE(1405, "该收藏夹未公开"),

    /** Blog module */
    BLOG_NOT_FOUND(1501, "博客不存在"),
    BLOG_FORBIDDEN(1502, "无权操作该博客"),
    BLOG_REF_INVALID(1503, "博客关联对象不存在或不可见"),
    BLOG_LIKE_DUPLICATE(1504, "已经点过赞了"),
    /** Comment module */
    COMMENT_TARGET_INVALID(1601, "Comment target invalid"),
    COMMENT_NOT_FOUND(1602, "Comment not found"),
    ;

    private final Integer code;
    private final String message;
}
