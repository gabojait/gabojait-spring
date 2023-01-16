package com.inuappcenter.gabojaitspring.config;

import com.fasterxml.classmate.TypeResolver;
import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.exception.DefaultExceptionResponseDto;
import com.inuappcenter.gabojaitspring.profile.dto.*;
import com.inuappcenter.gabojaitspring.project.dto.ProjectAbstractResponseDto;
import com.inuappcenter.gabojaitspring.project.dto.ProjectDefaultResponseDto;
import com.inuappcenter.gabojaitspring.user.dto.ContactDefaultResponseDto;
import com.inuappcenter.gabojaitspring.user.dto.UserDefaultResponseDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
@EnableWebMvc
public class SwaggerConfiguration {

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("JWT", authorizationScopes));
    }

    @Bean
    public Docket api() {
        TypeResolver typeResolver = new TypeResolver();

        return new Docket(DocumentationType.SWAGGER_2)
                .additionalModels(typeResolver.resolve(DefaultResponseDto.class))
                .additionalModels(typeResolver.resolve(DefaultExceptionResponseDto.class))
                .additionalModels(typeResolver.resolve(ContactDefaultResponseDto.class))
                .additionalModels(typeResolver.resolve(UserDefaultResponseDto.class))
                .additionalModels(typeResolver.resolve(ProfileDefaultResponseDto.class))
                .additionalModels(typeResolver.resolve(ProfileAbstractResponseDto.class))
                .additionalModels(typeResolver.resolve(ProfileManyResponseDto.class))
                .additionalModels(typeResolver.resolve(EducationDefaultResponseDto.class))
                .additionalModels(typeResolver.resolve(SkillDefaultResponseDto.class))
                .additionalModels(typeResolver.resolve(WorkDefaultResponseDto.class))
                .additionalModels(typeResolver.resolve(PortfolioDefaultResponseDto.class))
                .additionalModels(typeResolver.resolve(ProjectDefaultResponseDto.class))
                .additionalModels(typeResolver.resolve(ProjectAbstractResponseDto.class))
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(List.of(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.inuappcenter.gabojait"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Gabojait API")
                .version("1.0")
                .build();
    }
}
