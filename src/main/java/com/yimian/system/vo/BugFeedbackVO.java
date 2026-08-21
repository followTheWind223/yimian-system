package com.yimian.system.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BugFeedbackVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String content;
    private String pageUrl;
    private String contact;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
