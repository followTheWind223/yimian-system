package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tag")
public class Tag extends BaseEntity {

    /** 标签名称 */
    private String name;

    /** 排序号 */
    private Integer sort;

    /** 标签颜色 */
    private String color;
}
