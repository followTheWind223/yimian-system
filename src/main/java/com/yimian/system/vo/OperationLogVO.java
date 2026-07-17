package com.yimian.system.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志响应 VO
 */
@Data
public class OperationLogVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String username;
    private String module;
    private String moduleName;
    private String operation;
    private String description;
    private String method;
    private String requestUri;
    private String classMethod;
    private String requestParams;
    private String ip;
    private String userAgent;
    private Integer responseCode;
    private Integer resultCode;
    private String resultMsg;
    private String errorMsg;
    private Long duration;
    private Integer status;
    private LocalDateTime createdAt;
}
