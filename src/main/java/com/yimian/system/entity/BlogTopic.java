package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_blog_topic_relation")
public class BlogTopic extends BaseEntity {

    private Long blogId;
    private Long topicId;
}
