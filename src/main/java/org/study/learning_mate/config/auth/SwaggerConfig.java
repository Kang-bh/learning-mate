package org.study.learning_mate.config.auth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
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
                .addServersItem(server);
    }

//    @Bean
//    public OperationCustomizer operationCustomizer() {
//        return (operation, handlerMethod) -> {
//            this.addResponseBodyWrapperSchemaExample(operation, SuccessResponse.class, "data");
//            return operation;
//        };
//    }
//
//    private void addResponseBodyWrapperSchemaExample(Operation operation, Class<?> type, String wrapFieldName) {
//        final Content content = operation.getResponses().get("200").getContent();
//        if (content != null) {
//            content.keySet()
//                    .forEach(mediaTypeKey -> {
//                        final MediaType mediaType = content.get(mediaTypeKey);
//                        mediaType.schema(wrapSchema(mediaType.getSchema(), type, wrapFieldName));
//                    });
//        }
//    }
//
//    @SneakyThrows
//    private <T> Schema<T> wrapSchema(Schema<?> originalSchema, Class<T> type, String wrapFieldName) {
//        final Schema<T> wrapperSchema = new Schema<>();
//        final T instance = type.getDeclaredConstructor().newInstance();
//
//        for (Field field : type.getDeclaredFields()) {
//            field.setAccessible(true);
//            wrapperSchema.addProperty(field.getName(), new Schema<>().example(field.get(instance)));
//            field.setAccessible(false);
//        }
//        wrapperSchema.addProperty(wrapFieldName, originalSchema);
//        return wrapperSchema;
//    }

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
