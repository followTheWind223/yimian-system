package com.yimian.system.service.impl;

import com.yimian.system.common.exception.BusinessException;
import com.yimian.system.common.result.ResultCode;
import com.yimian.system.config.FileProperties;
import com.yimian.system.service.FileService;
import com.yimian.system.vo.FileVO;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnExpression("#{environment.getProperty('oss.enabled') != 'true'}")
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileProperties fileProperties;

    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public FileVO upload(MultipartFile file, String scene) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_EMPTY);
        }

        String safeScene = sanitizeScene(scene);
        if (!StringUtils.hasText(safeScene)) {
            safeScene = "common";
        }

        String original = file.getOriginalFilename();
        String extension = getExtension(original);
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        String dateDir = LocalDate.now().format(DATE_DIR);
        String relativePath = safeScene + "/" + dateDir + "/" + fileName;

        Path targetPath = Paths.get(fileProperties.getBasePath(), relativePath);
        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved locally: {}", targetPath);
        } catch (IOException e) {
            log.error("Failed to save file: {}", e.getMessage());
            throw new BusinessException(ResultCode.FILE_SAVE_FAILED);
        }

        String accessUrl = fileProperties.getUrlPrefix() + "/" + relativePath.replace("\\", "/");

        return FileVO.builder()
                .originalName(original)
                .fileName(fileName)
                .path(relativePath)
                .url(accessUrl)
                .size(file.getSize())
                .contentType(file.getContentType())
                .extension(extension)
                .build();
    }

    @Override
    public Resource load(String relativePath) {
        try {
            Path filePath = Paths.get(fileProperties.getBasePath(), relativePath).normalize();
            Resource resource = new UrlResource(filePath.toUri());
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
