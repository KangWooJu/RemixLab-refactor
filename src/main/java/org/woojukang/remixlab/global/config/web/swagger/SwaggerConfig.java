package org.woojukang.remixlab.global.config.web.swagger;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        // 보안 스킴 적용 -> Swagger에서 API 호출 시 , JWT 토큰 필요

        // OpenAPI에서 사용할 재사용 가능한 구성 요소
        Components components = new Components()
                .addSecuritySchemes(jwt, new SecurityScheme()
                        .name(jwt)
                        .type(SecurityScheme.Type.HTTP)// http 인증방식 사용
                        .scheme("bearer") // Bearer 토큰 방식
                        .bearerFormat("JWT") // 토큰 형식 JWT 지정
                );

        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                .addSecurityItem(securityRequirement);
    }

    private Info apiInfo() {
        return new Info()
                .title("프로젝트 API")
                .description("설명")
                .version("1.0.0");
    }
}
