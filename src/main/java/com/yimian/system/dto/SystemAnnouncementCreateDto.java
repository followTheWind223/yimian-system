package com.yimian.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "系统公告创建请求")
public class SystemAnnouncementCreateDto {

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 120, message = "公告标题最长120字")
    @Schema(description = "公告标题")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Size(max = 2000, message = "公告内容最长2000字")
    @Schema(description = "公告内容")
    private String content;

    @NotNull(message = "是否重要不能为空")
    @Schema(description = "是否重要，重要公告会在用户端弹窗")
    private Boolean important;
}
