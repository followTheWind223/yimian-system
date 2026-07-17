package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_favorite_item")
public class FavoriteItem extends BaseEntity {

    private Long folderId;

    private String itemType;

    private Long targetId;

    private Long knowledgeId;

    private Long blogId;

    private Integer sort;
}
