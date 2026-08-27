package com.yimian.system.service;

import com.yimian.system.dto.AgentModelCreateDto;
import com.yimian.system.dto.AgentModelUpdateDto;
import com.yimian.system.dto.AgentProviderCreateDto;
import com.yimian.system.dto.AgentProviderUpdateDto;
import com.yimian.system.dto.AgentProfileModelsUpdateDto;
import com.yimian.system.vo.AgentModelVO;
import com.yimian.system.vo.AgentProfileModelVO;
import com.yimian.system.vo.AgentProfileVO;
import com.yimian.system.vo.AgentProviderTestVO;
import com.yimian.system.vo.AgentProviderVO;
import java.util.List;

public interface AgentModelControlService {
    List<AgentProviderVO> listProviders();
    AgentProviderVO createProvider(AgentProviderCreateDto request);
    AgentProviderVO updateProvider(Long id, AgentProviderUpdateDto request);
    AgentProviderTestVO testProviderConfig(AgentProviderCreateDto request);
    AgentProviderTestVO testProvider(Long id);
    List<AgentModelVO> listModels();
    AgentModelVO createModel(AgentModelCreateDto request);
    AgentModelVO updateModel(Long id, AgentModelUpdateDto request);
    List<AgentProfileVO> listProfiles();
    List<AgentProfileModelVO> listProfileModels(String profileCode);
    List<AgentProfileModelVO> updateProfileModels(String profileCode, AgentProfileModelsUpdateDto request);
}
