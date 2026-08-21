package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Bug feedback submitted by users.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_bug_feedback")
public class BugFeedback extends BaseEntity {

    private Long userId;

    private String username;

    private String title;

    private String content;

    private String pageUrl;

    private String contact;

    /** 0=pending, 1=processing, 2=resolved, 3=ignored */
    private Integer status;
}
