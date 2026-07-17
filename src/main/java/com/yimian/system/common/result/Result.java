package com.yimian.system.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一 API 响应结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private Integer code;

    /** 消息 */
    private String message;

    /** 数据 */
    private T data;

    /** 时间戳 */
    private Long timestamp;

    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== 成功 ====================

    public static <T> Result<T> success() {
        Result<T> r = new Result<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.message = ResultCode.SUCCESS.getMessage();
        return r;
    }

    public static <T> Result<T> success(T data) {
        Result<T> r = success();
        r.data = data;
        return r;
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> r = success(data);
        r.message = message;
        return r;
    }

    // ==================== 失败 ====================

    public static <T> Result<T> error() {
        Result<T> r = new Result<>();
        r.code = ResultCode.ERROR.getCode();
        r.message = ResultCode.ERROR.getMessage();
        return r;
    }

    public static <T> Result<T> error(String message) {
        Result<T> r = error();
        r.message = message;
        return r;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        Result<T> r = new Result<>();
        r.code = resultCode.getCode();
        r.message = resultCode.getMessage();
        return r;
    }

    public static <T> Result<T> error(ResultCode resultCode, String message) {
        Result<T> r = error(resultCode);
        r.message = message;
        return r;
    }

    // ==================== 判断 ====================

    public boolean isSuccess() {
        return this.code != null && this.code.equals(ResultCode.SUCCESS.getCode());
    }
}
