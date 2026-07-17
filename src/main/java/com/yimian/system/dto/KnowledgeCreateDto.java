package com.yimian.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 知识题目创建/提交 DTO
 */
@Data
@Schema(description = "知识题目创建请求")
public class KnowledgeCreateDto {

    @NotBlank(message = "题目不能为空")
    @Size(max = 200, message = "题目最长200字")
    @Schema(description = "题目", example = "HashMap底层原理")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Schema(description = "正文（Markdown原文）")
    private String content;

    @Schema(description = "难度：1=简单 2=中等 3=困难", example = "2")
    private Integer difficulty;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;
}
