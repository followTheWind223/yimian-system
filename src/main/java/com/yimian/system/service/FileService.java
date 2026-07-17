package com.yimian.system.service;

import com.yimian.system.vo.FileVO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务
 * 通用上传工具：按业务场景分目录存储，不校验文件类型（由业务层判断）
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param file  multipart 文件
     * @param scene 业务场景，作为一级子目录，如 avatar / md
     * @return 文件信息（含访问 URL）
     */
    FileVO upload(MultipartFile file, String scene);

    /**
     * 根据相对路径加载文件资源（用于流式返回）
     *
     * @param relativePath 相对存储路径，如 avatar/2026/07/13/xxx.jpg
     * @return 文件 Resource
     */
    Resource load(String relativePath);
}
