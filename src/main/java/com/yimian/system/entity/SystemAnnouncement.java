package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_system_announcement")
public class SystemAnnouncement extends BaseEntity {

    private String title;

    private String content;

    private Boolean important;

    private Boolean enabled;

    private Long creatorId;
}
