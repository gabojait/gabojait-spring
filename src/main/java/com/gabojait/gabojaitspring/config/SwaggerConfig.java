package com.gabojait.gabojaitspring.config;

import com.fasterxml.classmate.TypeResolver;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.common.dto.ExceptionResDto;
import com.gabojait.gabojaitspring.offer.dto.res.OfferDefaultResDto;
import com.gabojait.gabojaitspring.profile.dto.res.*;
import com.gabojait.gabojaitspring.review.dto.res.ReviewDefaultResDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamAbstractResDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamDefaultResDto;
import com.gabojait.gabojaitspring.team.dto.res.TeamDetailResDto;
import com.gabojait.gabojaitspring.user.dto.res.ContactDefaultResDto;
import com.gabojait.gabojaitspring.user.dto.res.UserDefaultResDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
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
public class SwaggerConfig {

    private ApiKey accessToken() {
        return new ApiKey("access token", "Authorization", "header");
    }

    private ApiKey refreshToken() {
        return new ApiKey("refresh token", "Refresh-Token", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes = {
                new AuthorizationScope("global", "accessEverything")
        };
        SecurityReference[] references = {
                new SecurityReference("access token", authorizationScopes),
                new SecurityReference("refresh token", authorizationScopes)
        };
        return List.of(references);
    }

    @Bean
    public Docket api() {
        TypeResolver typeResolver = new TypeResolver();

        return new Docket(DocumentationType.SWAGGER_2)
                .additionalModels(typeResolver.resolve(DefaultResDto.class))
                .additionalModels(typeResolver.resolve(ExceptionResDto.class))
                .additionalModels(typeResolver.resolve(ContactDefaultResDto.class))
                .additionalModels(typeResolver.resolve(UserDefaultResDto.class))
                .additionalModels(typeResolver.resolve(ProfileDefaultResDto.class))
                .additionalModels(typeResolver.resolve(ProfileDetailResDto.class))
                .additionalModels(typeResolver.resolve(ProfileAbstractResDto.class))
                .additionalModels(typeResolver.resolve(EducationDefaultResDto.class))
                .additionalModels(typeResolver.resolve(PortfolioDefaultResDto.class))
                .additionalModels(typeResolver.resolve(SkillDefaultResDto.class))
                .additionalModels(typeResolver.resolve(WorkDefaultResDto.class))
                .additionalModels(typeResolver.resolve(ReviewDefaultResDto.class))
                .additionalModels(typeResolver.resolve(TeamDefaultResDto.class))
                .additionalModels(typeResolver.resolve(TeamAbstractResDto.class))
                .additionalModels(typeResolver.resolve(TeamDetailResDto.class))
                .additionalModels(typeResolver.resolve(OfferDefaultResDto.class))
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(List.of(accessToken(), refreshToken()))
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Gabojait API")
                .version("V1.0.0")
                .build();
    }
}
