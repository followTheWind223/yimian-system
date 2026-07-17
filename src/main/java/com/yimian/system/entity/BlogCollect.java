package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_blog_collect")
public class BlogCollect extends BaseEntity {

    private Long blogId;
    private Long folderId;
    private Long userId;
}
