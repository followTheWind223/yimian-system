package com.yimian.system.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传响应 VO
 */
@Data
@Builder
public class FileVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 原始文件名 */
    private String originalName;

    /** 存储后的文件名（uuid + 扩展名） */
    private String fileName;

    /** 相对存储路径，如 avatar/2026/07/13/xxx.jpg（用于拼接访问 URL） */
    private String path;

    /** 完整访问 URL，如 /api/file/avatar/2026/07/13/xxx.jpg */
    private String url;

    /** 文件大小（字节） */
    private long size;

    /** 文件 MIME 类型 */
    private String contentType;

    /** 文件扩展名（不含点，如 jpg） */
    private String extension;
}
