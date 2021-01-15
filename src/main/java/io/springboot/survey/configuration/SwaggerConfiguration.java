package io.springboot.survey.configuration;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

import static io.springboot.survey.utils.Constants.AuthorizationModuleConstant.JWT;
import static io.springboot.survey.utils.Constants.SwaggerConstant.*;

@Configuration
public class SwaggerConfiguration {
    final Parameter authHeader = new ParameterBuilder()
            .parameterType(HEADER)
            .name(JWT).required(true)
            .modelRef(new ModelRef(TYPE_STRING))
            .build();
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any()).build().pathMapping("/")
                .globalOperationParameters(Collections.singletonList(authHeader))
                .apiInfo(apiInfo()).useDefaultResponseMessages(false);
    }

    @Bean
    public ApiInfo apiInfo() {
        final ApiInfoBuilder builder = new ApiInfoBuilder();
        builder.title(SURVEY_MANAGEMENT_TOOL).version(APPLICATION_VERSION)
                .description(APPLICATION_DESCRIPTION);
        return builder.build();
    }

}
