package com.gabojait.gabojaitspring.config;

import com.fasterxml.classmate.TypeResolver;
import com.gabojait.gabojaitspring.api.dto.common.response.*;
import com.gabojait.gabojaitspring.api.dto.favorite.response.FavoriteTeamResponse;
import com.gabojait.gabojaitspring.api.dto.favorite.response.FavoriteUserResponse;
import com.gabojait.gabojaitspring.api.dto.notification.response.NotificationDefaultResponse;
import com.gabojait.gabojaitspring.api.dto.offer.response.OfferAbstractResponse;
import com.gabojait.gabojaitspring.api.dto.offer.response.OfferDefaultResponse;
import com.gabojait.gabojaitspring.api.dto.profile.response.*;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewDefaultResponse;
import com.gabojait.gabojaitspring.api.dto.team.response.TeamAbstractResponse;
import com.gabojait.gabojaitspring.api.dto.team.response.TeamDefaultResponse;
import com.gabojait.gabojaitspring.api.dto.team.response.TeamMemberDefaultResponse;
import com.gabojait.gabojaitspring.api.dto.team.response.TeamOfferFavoriteResponse;
import com.gabojait.gabojaitspring.api.dto.user.response.ContactDefaultResponse;
import com.gabojait.gabojaitspring.api.dto.user.response.UserDefaultResponse;
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

    @Bean
    public Docket api() {
        TypeResolver typeResolver = new TypeResolver();

        return new Docket(DocumentationType.SWAGGER_2)
                .additionalModels(typeResolver.resolve(DefaultNoResponse.class))
                .additionalModels(typeResolver.resolve(DefaultSingleResponse.class))
                .additionalModels(typeResolver.resolve(DefaultMultiResponse.class))
                .additionalModels(typeResolver.resolve(PageData.class))
                .additionalModels(typeResolver.resolve(ExceptionResponse.class))
                .additionalModels(typeResolver.resolve(ContactDefaultResponse.class))
                .additionalModels(typeResolver.resolve(UserDefaultResponse.class))
                .additionalModels(typeResolver.resolve(EducationDefaultResponse.class))
                .additionalModels(typeResolver.resolve(PortfolioDefaultResponse.class))
                .additionalModels(typeResolver.resolve(PortfolioUrlResponse.class))
                .additionalModels(typeResolver.resolve(SkillDefaultResponse.class))
                .additionalModels(typeResolver.resolve(WorkDefaultResponse.class))
                .additionalModels(typeResolver.resolve(ProfileAbstractResponse.class))
                .additionalModels(typeResolver.resolve(ProfileDefaultResponse.class))
                .additionalModels(typeResolver.resolve(ProfileDetailResponse.class))
                .additionalModels(typeResolver.resolve(ProfileOfferResponse.class))
                .additionalModels(typeResolver.resolve(TeamAbstractResponse.class))
                .additionalModels(typeResolver.resolve(TeamDefaultResponse.class))
                .additionalModels(typeResolver.resolve(TeamOfferFavoriteResponse.class))
                .additionalModels(typeResolver.resolve(TeamMemberDefaultResponse.class))
                .additionalModels(typeResolver.resolve(ReviewDefaultResponse.class))
                .additionalModels(typeResolver.resolve(OfferAbstractResponse.class))
                .additionalModels(typeResolver.resolve(OfferDefaultResponse.class))
                .additionalModels(typeResolver.resolve(NotificationDefaultResponse.class))
                .additionalModels(typeResolver.resolve(FavoriteTeamResponse.class))
                .additionalModels(typeResolver.resolve(FavoriteUserResponse.class))
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(List.of(accessToken(), refreshToken()))
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build();
    }


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

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("가보자IT API")
                .version("V1.0.0")
                .description("\t<문서>\n" +
                        "1. 개인정보처리방침 -> /docs/privacy.html\n" +
                        "2. 서비스이용약관 -> /docs/service.html")
                .build();
    }
}
