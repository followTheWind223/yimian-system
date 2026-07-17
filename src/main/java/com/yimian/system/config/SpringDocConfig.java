package com.yimian.system.config;

import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * SpringDoc / Swagger UI 配置
 */
@Configuration
public class SpringDocConfig {

    @PostConstruct
    public void init() {
        // PageInfo 类型替换为 Object，避免 Springdoc 解析泛型时异常
        SpringDocUtils.getConfig().replaceWithClass(PageInfo.class, Object.class);
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("易面 - 面试助手 API")
                        .version("1.0.0")
                        .description("智能面试平台后端接口"))
                .components(new Components()
                        .addSecuritySchemes("Bearer", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"));
    }
}
