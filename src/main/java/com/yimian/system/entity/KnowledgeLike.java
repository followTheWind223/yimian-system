package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_knowledge_like")
public class KnowledgeLike extends BaseEntity {

    private Long knowledgeId;
    private Long userId;
}
