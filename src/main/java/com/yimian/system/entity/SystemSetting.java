package com.yimian.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统运行配置项。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_system_setting")
public class SystemSetting extends BaseEntity {

    /** 配置键 */
    private String settingKey;

    /** 配置值 */
    private String settingValue;

    /** 配置说明 */
    private String remark;
}
