package com.yimian.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 知识题目列表查询 DTO
 */
@Data
@Schema(description = "知识题目列表查询")
public class KnowledgeQueryDto {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;

    @Schema(description = "关键词搜索（标题/内容）")
    private String keyword;

    @Schema(description = "难度：1=简单 2=中等 3=困难")
    private Integer difficulty;

    @Schema(description = "标签ID筛选")
    private Long tagId;

    @Schema(description = "状态筛选（管理员用）")
    private Integer status;
}
