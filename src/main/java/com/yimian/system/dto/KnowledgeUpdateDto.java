package com.yimian.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 知识题目编辑 DTO
 */
@Data
@Schema(description = "知识题目编辑请求")
public class KnowledgeUpdateDto {

    @Size(max = 200, message = "题目最长200字")
    @Schema(description = "题目")
    private String title;

    @Schema(description = "正文（Markdown原文）")
    private String content;

    @Schema(description = "难度：1=简单 2=中等 3=困难")
    private Integer difficulty;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;
}
