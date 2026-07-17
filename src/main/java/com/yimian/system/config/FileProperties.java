package com.yimian.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件存储配置
 * 对应 application.yml 中 file.* 配置项
 */
@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    /** 存储根目录（相对路径会基于工作目录，建议用绝对路径） */
    private String basePath = "./oss";

    /** 文件访问 URL 前缀，如 /api/file */
    private String urlPrefix = "/api/file";

    /** 单文件最大字节数（-1 表示不限制，仍受 spring.servlet.multipart.max-file-size 约束） */
    private long maxSize = -1;
}
