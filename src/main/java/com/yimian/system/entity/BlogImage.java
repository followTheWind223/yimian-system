package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_blog_image")
public class BlogImage extends BaseEntity {

    private Long blogId;
    private String url;
    private Integer sort;
}
