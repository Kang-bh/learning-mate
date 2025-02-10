package org.study.learning_mate.config.auth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@SecurityScheme(
//        name = "oauth2-kakao",
//        type = SecuritySchemeType.OAUTH2,
//        flows = @OAuthFlows(
//                authorizationCode = @OAuthFlow(
//                        authorizationUrl = "http://52.78.81.43/oauth2/authorization/google"
//                )
//        )
//)
@io.swagger.v3.oas.annotations.security.SecurityScheme(name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "Bearer")
@OpenAPIDefinition
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        Server server = new Server().url("/");

        return new OpenAPI()
                .info(getSwaggerInfo())
                .components(
                        new Components()
                                .addSecuritySchemes("accessToken", new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .name("Authorization")
                                        .bearerFormat("JWT")
                                )
                )
                .addServersItem(server);
    }


    private Info getSwaggerInfo() {
        License license = new License();
        license.setName("MIT License");
        license.setUrl("https://opensource.org/licenses/MIT");

        return new Info()
                .title("Learning Mate API Document")
                .description("Learning Mate API 문서 입니다.")
                .version("v1.0.0")
                .license(license);
    }


}
