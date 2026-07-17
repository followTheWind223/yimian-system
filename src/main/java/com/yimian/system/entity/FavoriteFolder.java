package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_favorite_folder")
public class FavoriteFolder extends BaseEntity {

    private String name;

    private String description;

    private Long userId;

    private Integer isPublic;

    private String coverImage;

    private Integer itemCount;

    private Integer viewCount;
}
