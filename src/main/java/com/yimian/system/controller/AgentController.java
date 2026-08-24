package com.yimian.system.controller;

import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.AgentChatDto;
import com.yimian.system.security.JwtUserDetails;
import com.yimian.system.service.AgentProxyService;
import com.yimian.system.vo.AgentChatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Tag(name = "Agent module", description = "Authenticated Agent service proxy")
@Validated
@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentProxyService agentProxyService;

    @OperationLog(
            module = "AGENT",
            operation = "Chat",
            description = "Agent chat session #{#dto.sessionId}",
            logParams = false
    )
    @Operation(summary = "Agent chat")
    @PostMapping("/chat")
    public Result<AgentChatVO> chat(@Valid @RequestBody AgentChatDto dto) {
        return Result.success(agentProxyService.chat(getCurrentUserId(), dto));
    }

    @OperationLog(
            module = "AGENT",
            operation = "Stream chat",
            description = "Agent stream session #{#dto.sessionId}",
            logParams = false
    )
    @Operation(summary = "Agent stream chat")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> streamChat(@Valid @RequestBody AgentChatDto dto) {
        StreamingResponseBody body = agentProxyService.streamChat(getCurrentUserId(), dto);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONNECTION, "keep-alive")
                .header("X-Accel-Buffering", "no")
                .body(body);
    }

    @OperationLog(
            module = "AGENT",
            operation = "Clear session",
            description = "Clear Agent session #{#sessionId}",
            logParams = false
    )
    @Operation(summary = "Clear Agent session")
    @DeleteMapping("/sessions/{sessionId}")
    public Result<Void> clearSession(
            @PathVariable
            @Size(max = 64, message = "会话 ID 最长 64 个字符")
            @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9_-]*$", message = "会话 ID 格式不正确")
            String sessionId) {
        agentProxyService.clearSession(getCurrentUserId(), sessionId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof JwtUserDetails userDetails) {
            return userDetails.getUser().getId();
        }
        throw new IllegalStateException("Cannot resolve current user");
    }
}
