package com.inuappcenter.gabojaitspring.config;

import com.fasterxml.classmate.TypeResolver;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.DefaultExceptionResDto;
import com.inuappcenter.gabojaitspring.profile.dto.res.*;
import com.inuappcenter.gabojaitspring.review.dto.res.ReviewDefaultResDto;
import com.inuappcenter.gabojaitspring.team.dto.res.OfferDefaultResDto;
import com.inuappcenter.gabojaitspring.team.dto.res.TeamDefaultResDto;
import com.inuappcenter.gabojaitspring.user.dto.res.ContactDefaultResDto;
import com.inuappcenter.gabojaitspring.user.dto.res.UserDefaultResDto;
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
                .additionalModels(typeResolver.resolve(DefaultResDto.class))
                .additionalModels(typeResolver.resolve(DefaultExceptionResDto.class))
                .additionalModels(typeResolver.resolve(ContactDefaultResDto.class))
                .additionalModels(typeResolver.resolve(UserDefaultResDto.class))
                .additionalModels(typeResolver.resolve(UserProfileDefaultResDto.class))
                .additionalModels(typeResolver.resolve(UserProfileAbstractResDto.class))
                .additionalModels(typeResolver.resolve(EducationDefaultResDto.class))
                .additionalModels(typeResolver.resolve(PortfolioDefaultResDto.class))
                .additionalModels(typeResolver.resolve(SkillDefaultResDto.class))
                .additionalModels(typeResolver.resolve(WorkDefaultResDto.class))
                .additionalModels(typeResolver.resolve(TeamDefaultResDto.class))
                .additionalModels(typeResolver.resolve(OfferDefaultResDto.class))
                .additionalModels(typeResolver.resolve(ReviewDefaultResDto.class))
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
