package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_blog_topic_category")
public class Topic extends BaseEntity {

    private String name;
    private String description;
    private String color;
    private Integer sort;
    private Integer blogCount;
}
