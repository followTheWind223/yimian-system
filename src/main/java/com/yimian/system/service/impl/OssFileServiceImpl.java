package com.yimian.system.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.config.OssProperties;
import com.yimian.system.service.FileService;
import com.yimian.system.vo.FileVO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnExpression("#{environment.getProperty('oss.enabled') == 'true'}")
@RequiredArgsConstructor
public class OssFileServiceImpl implements FileService {

    private final OssProperties ossProperties;

    private OSS ossClient;

    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );
        log.info("OSS client initialized: endpoint={}, bucket={}",
                ossProperties.getEndpoint(), ossProperties.getBucketName());
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("OSS client shutdown");
        }
    }

    @Override
    public FileVO upload(MultipartFile file, String scene) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_EMPTY);
        }

        String safeScene = sanitizeScene(scene);
        if (!StringUtils.hasText(safeScene)) {
            throw new BusinessException(ResultCode.FILE_ILLEGAL_PATH, "Invalid scene");
        }

        String original = file.getOriginalFilename();
        String extension = getExtension(original);
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        String dateDir = LocalDate.now().format(DATE_DIR);
        String objectKey = safeScene + "/" + dateDir + "/" + fileName;

        try {
            PutObjectResult result = ossClient.putObject(
                    ossProperties.getBucketName(),
                    objectKey,
                    file.getInputStream()
            );
            log.info("OSS upload success: key={}, etag={}", objectKey, result.getETag());
        } catch (IOException e) {
            log.error("OSS upload failed: {}", e.getMessage());
            throw new BusinessException(ResultCode.FILE_SAVE_FAILED);
        }

        String url = ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/" + objectKey;

        return FileVO.builder()
                .originalName(original)
                .fileName(fileName)
                .path(objectKey)
                .url("https://" + url)
                .size(file.getSize())
                .contentType(file.getContentType())
                .extension(extension)
                .build();
    }

    @Override
    public Resource load(String relativePath) {
        String url = "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/" + relativePath;
        try {
            Resource resource = new UrlResource(new URL(url));
            if (!resource.exists() || !resource.isReadable()) {
                throw new BusinessException(ResultCode.FILE_NOT_FOUND);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new BusinessException(ResultCode.FILE_ILLEGAL_PATH);
        }
    }

    private String sanitizeScene(String scene) {
        if (scene == null) return "";
        String s = scene.trim();
        if (!s.matches("[A-Za-z0-9_-]+")) return "";
        return s;
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return "";
        return filename.substring(dot + 1).toLowerCase();
    }
}
