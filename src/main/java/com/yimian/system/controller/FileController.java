package com.yimian.system.controller;

import com.yimian.system.common.result.Result;
import com.yimian.system.service.FileService;
import com.yimian.system.vo.FileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件上传/读取控制器
 * 通用工具接口：不做文件类型判断，由业务层自行限制
 */
@Tag(name = "文件管理", description = "通用文件上传与读取")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<FileVO> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "scene", defaultValue = "common") String scene) {
        return Result.success(fileService.upload(file, scene));
    }

    @Operation(summary = "读取文件（流式返回）")
    @GetMapping("/{scene}/{date:[\\d]{4}}/{month:[\\d]{2}}/{day:[\\d]{2}}/{file:.+}")
    public ResponseEntity<Resource> read(@PathVariable String scene,
                                         @PathVariable String date,
                                         @PathVariable String month,
                                         @PathVariable String day,
                                         @PathVariable String file) {
        String relativePath = scene + "/" + date + "/" + month + "/" + day + "/" + file;
        Resource resource = fileService.load(relativePath);

        String filename = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename*=UTF-8''" + filename)
                .body(resource);
    }
}
