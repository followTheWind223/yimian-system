package com.yimian.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Agent 对话请求")
public class AgentChatDto {

    @NotBlank(message = "会话 ID 不能为空")
    @Size(max = 64, message = "会话 ID 最长 64 个字符")
    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9_-]*$", message = "会话 ID 只能包含字母、数字、下划线和连字符")
    @Schema(description = "前端生成并在同一会话中复用的 ID", example = "chat_01JABCDEF")
    private String sessionId;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 4000, message = "单条消息最长 4000 个字符")
    @Schema(description = "用户发送给 Agent 的消息")
    private String message;
}
