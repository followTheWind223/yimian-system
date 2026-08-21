package com.yimian.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "用户反馈创建请求")
public class BugFeedbackCreateDto {

    @NotBlank(message = "反馈标题不能为空")
    @Size(max = 120, message = "反馈标题最长120字")
    @Schema(description = "反馈标题", example = "博客发布后详情页提示不存在")
    private String title;

    @NotBlank(message = "问题描述不能为空")
    @Size(max = 2000, message = "问题描述最长2000字")
    @Schema(description = "问题描述")
    private String content;

    @Size(max = 500, message = "页面地址最长500字")
    @Schema(description = "发生问题的页面地址")
    private String pageUrl;

    @Size(max = 120, message = "联系方式最长120字")
    @Schema(description = "可选联系方式")
    private String contact;
}
