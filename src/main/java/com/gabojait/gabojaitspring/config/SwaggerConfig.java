package com.gabojait.gabojaitspring.config;

import com.fasterxml.classmate.TypeResolver;
import com.gabojait.gabojaitspring.api.dto.favorite.response.FavoriteSkillResponse;
import com.gabojait.gabojaitspring.api.dto.favorite.response.FavoriteTeamPageResponse;
import com.gabojait.gabojaitspring.api.dto.favorite.response.FavoriteUserPageResponse;
import com.gabojait.gabojaitspring.api.dto.notification.response.NotificationPageResponse;
import com.gabojait.gabojaitspring.api.dto.profile.response.*;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewFindAllTeamResponse;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewFindTeamResponse;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewPageResponse;
import com.gabojait.gabojaitspring.api.dto.review.response.ReviewTeamMemberResponse;
import com.gabojait.gabojaitspring.api.dto.team.response.*;
import com.gabojait.gabojaitspring.api.dto.user.response.UserContactResponse;
import com.gabojait.gabojaitspring.api.dto.user.response.UserFindMyselfResponse;
import com.gabojait.gabojaitspring.api.dto.user.response.UserLoginResponse;
import com.gabojait.gabojaitspring.api.dto.user.response.UserRegisterResponse;
import com.gabojait.gabojaitspring.common.response.*;
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
                .additionalModels(typeResolver.resolve(UserRegisterResponse.class))
                .additionalModels(typeResolver.resolve(UserLoginResponse.class))
                .additionalModels(typeResolver.resolve(UserFindMyselfResponse.class))
                .additionalModels(typeResolver.resolve(UserContactResponse.class))
                .additionalModels(typeResolver.resolve(EducationResponse.class))
                .additionalModels(typeResolver.resolve(PortfolioResponse.class))
                .additionalModels(typeResolver.resolve(SkillResponse.class))
                .additionalModels(typeResolver.resolve(WorkResponse.class))
                .additionalModels(typeResolver.resolve(PortfolioResponse.class))
                .additionalModels(typeResolver.resolve(ProfileReviewResponse.class))
                .additionalModels(typeResolver.resolve(ProfileTeamResponse.class))
                .additionalModels(typeResolver.resolve(ProfileOfferResponse.class))
                .additionalModels(typeResolver.resolve(PortfolioUrlResponse.class))
                .additionalModels(typeResolver.resolve(ProfileFindMyselfResponse.class))
                .additionalModels(typeResolver.resolve(ProfileFindOtherResponse.class))
                .additionalModels(typeResolver.resolve(ProfileImageResponse.class))
                .additionalModels(typeResolver.resolve(ProfileUpdateResponse.class))
                .additionalModels(typeResolver.resolve(TeamCreateResponse.class))
                .additionalModels(typeResolver.resolve(TeamFindResponse.class))
                .additionalModels(typeResolver.resolve(TeamMemberResponse.class))
                .additionalModels(typeResolver.resolve(TeamMyCurrentResponse.class))
                .additionalModels(typeResolver.resolve(TeamPageResponse.class))
                .additionalModels(typeResolver.resolve(TeamUpdateResponse.class))
                .additionalModels(typeResolver.resolve(TeamOfferResponse.class))
                .additionalModels(typeResolver.resolve(ReviewFindAllTeamResponse.class))
                .additionalModels(typeResolver.resolve(ReviewFindTeamResponse.class))
                .additionalModels(typeResolver.resolve(ReviewTeamMemberResponse.class))
                .additionalModels(typeResolver.resolve(ReviewPageResponse.class))
                .additionalModels(typeResolver.resolve(NotificationPageResponse.class))
                .additionalModels(typeResolver.resolve(FavoriteTeamPageResponse.class))
                .additionalModels(typeResolver.resolve(FavoriteUserPageResponse.class))
                .additionalModels(typeResolver.resolve(FavoriteSkillResponse.class))
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
                .title("가보자IT Alpha API")
                .version("V1.0.0")
                .description("\t<API 테스트를 위한 테스트 계정 Token 발급 방법>\n" +
                        "1. 개발\n" +
                        "2. 테스트 계정 토큰 발급\n" +
                        "3. tester-id = 1 ~ 100 (1 ~ 50 = 팀 리더 | 51 ~ 100 = 팀 없음 | 1 ~ 25 완료한 팀과 리뷰 있음)\n\n\n" +
                        "\t<요청시 Access Token 만료로 인한 401 응답을 받을 경우 재요청 방법>\n" +
                        "1. 요청을 보내고 401 응답을 받는다.\n" +
                        "2. 로그인 또는 회원 가입시 발급 받은 Refresh Token을 헤더에 \"Refresh-Token\": \"Bearer XXXXX\"와 같이 " +
                        "담아 토큰 재발급을 받는다.\n" +
                        "3. 재발급 받은 Access Token과 Refresh Token을 저장하고 새로운 Access Token으로 처음 보내려는 요청을 보낸다.\n" +
                        "- Refresh Token으로 토큰 재발급시 Refresh Token 만료로 인한 401 응답을 받을시 로그아웃 처리를 한다.\n\n\n" +
                        "\t<문서>\n" +
                        "1. 개인정보처리방침 -> /docs/privacy\n" +
                        "2. 서비스이용약관 -> /docs/service")
                .build();
    }
}
