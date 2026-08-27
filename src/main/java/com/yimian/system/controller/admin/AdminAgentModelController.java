package com.yimian.system.controller.admin;

import com.yimian.system.common.annotation.OperationLog;
import com.yimian.system.common.result.Result;
import com.yimian.system.dto.AgentModelCreateDto;
import com.yimian.system.dto.AgentModelUpdateDto;
import com.yimian.system.dto.AgentProviderCreateDto;
import com.yimian.system.dto.AgentProviderUpdateDto;
import com.yimian.system.dto.AgentProfileModelsUpdateDto;
import com.yimian.system.service.AgentModelControlService;
import com.yimian.system.vo.AgentModelVO;
import com.yimian.system.vo.AgentProfileModelVO;
import com.yimian.system.vo.AgentProfileVO;
import com.yimian.system.vo.AgentProviderTestVO;
import com.yimian.system.vo.AgentProviderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Agent model control", description = "Manage model providers and deployments")
@Validated
@RestController
@RequestMapping("/admin/agent/models")
@RequiredArgsConstructor
public class AdminAgentModelController {

    private final AgentModelControlService modelControlService;

    @Operation(summary = "List model providers")
    @GetMapping("/providers")
    @PreAuthorize("hasAuthority('agent:model:list')")
    public Result<List<AgentProviderVO>> providers() {
        return Result.success(modelControlService.listProviders());
    }

    @Operation(summary = "Create model provider")
    @OperationLog(module = "AGENT_MODEL", operation = "CREATE_PROVIDER", logParams = false)
    @PostMapping("/providers")
    @PreAuthorize("hasAuthority('agent:model:manage')")
    public Result<AgentProviderVO> createProvider(@Valid @RequestBody AgentProviderCreateDto request) {
        return Result.success(modelControlService.createProvider(request));
    }

    @Operation(summary = "Update model provider")
    @OperationLog(module = "AGENT_MODEL", operation = "UPDATE_PROVIDER", logParams = false)
    @PutMapping("/providers/{id}")
    @PreAuthorize("hasAuthority('agent:model:manage')")
    public Result<AgentProviderVO> updateProvider(
            @PathVariable @Positive Long id,
            @Valid @RequestBody AgentProviderUpdateDto request) {
        return Result.success(modelControlService.updateProvider(id, request));
    }

    @Operation(summary = "Test model provider connection")
    @PostMapping("/providers/{id}/test")
    @PreAuthorize("hasAuthority('agent:model:manage')")
    public Result<AgentProviderTestVO> testProvider(@PathVariable @Positive Long id) {
        return Result.success(modelControlService.testProvider(id));
    }

    @Operation(summary = "List models")
    @GetMapping
    @PreAuthorize("hasAuthority('agent:model:list')")
    public Result<List<AgentModelVO>> models() {
        return Result.success(modelControlService.listModels());
    }

    @Operation(summary = "Create model")
    @OperationLog(module = "AGENT_MODEL", operation = "CREATE_MODEL", logParams = false)
    @PostMapping
    @PreAuthorize("hasAuthority('agent:model:manage')")
    public Result<AgentModelVO> createModel(@Valid @RequestBody AgentModelCreateDto request) {
        return Result.success(modelControlService.createModel(request));
    }

    @Operation(summary = "Update model")
    @OperationLog(module = "AGENT_MODEL", operation = "UPDATE_MODEL", logParams = false)
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('agent:model:manage')")
    public Result<AgentModelVO> updateModel(
            @PathVariable @Positive Long id,
            @Valid @RequestBody AgentModelUpdateDto request) {
        return Result.success(modelControlService.updateModel(id, request));
    }

    @Operation(summary = "List Agent profiles")
    @GetMapping("/profiles")
    @PreAuthorize("hasAuthority('agent:model:list')")
    public Result<List<AgentProfileVO>> profiles() {
        return Result.success(modelControlService.listProfiles());
    }

    @Operation(summary = "List profile model bindings")
    @GetMapping("/profiles/{profileCode}/models")
    @PreAuthorize("hasAuthority('agent:model:list')")
    public Result<List<AgentProfileModelVO>> profileModels(@PathVariable String profileCode) {
        return Result.success(modelControlService.listProfileModels(profileCode));
    }

    @Operation(summary = "Update profile model bindings")
    @OperationLog(module = "AGENT_MODEL", operation = "UPDATE_PROFILE_MODELS", logParams = false)
    @PutMapping("/profiles/{profileCode}/models")
    @PreAuthorize("hasAuthority('agent:model:manage')")
    public Result<List<AgentProfileModelVO>> updateProfileModels(
            @PathVariable String profileCode,
            @Valid @RequestBody AgentProfileModelsUpdateDto request) {
        return Result.success(modelControlService.updateProfileModels(profileCode, request));
    }
}
