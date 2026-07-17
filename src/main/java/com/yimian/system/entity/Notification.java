package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notification")
public class Notification extends BaseEntity {

    private String type;
    private Long senderId;
    private Long receiverId;
    private String targetType;
    private Long targetId;
    private String title;
    private String content;
    private String extra;
    @com.baomidou.mybatisplus.annotation.TableField(value = "`read`")
    private Boolean read;
}
