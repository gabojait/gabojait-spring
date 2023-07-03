package com.gabojait.gabojaitspring.config;

import com.fasterxml.classmate.TypeResolver;
import com.gabojait.gabojaitspring.admin.dto.res.AdminAbstractResDto;
import com.gabojait.gabojaitspring.admin.dto.res.AdminDefaultResDto;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.common.dto.ExceptionResDto;
import com.gabojait.gabojaitspring.offer.dto.res.OfferDefaultResDto;
import com.gabojait.gabojaitspring.profile.dto.res.*;
import com.gabojait.gabojaitspring.review.dto.res.ReviewDefaultResDto;
import com.gabojait.gabojaitspring.team.dto.res.*;
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
                .additionalModels(typeResolver.resolve(ProfileAbstractResDto.class))
                .additionalModels(typeResolver.resolve(ProfileDefaultResDto.class))
                .additionalModels(typeResolver.resolve(ProfileFavoriteResDto.class))
                .additionalModels(typeResolver.resolve(ProfileSeekResDto.class))
                .additionalModels(typeResolver.resolve(EducationDefaultResDto.class))
                .additionalModels(typeResolver.resolve(PortfolioDefaultResDto.class))
                .additionalModels(typeResolver.resolve(SkillDefaultResDto.class))
                .additionalModels(typeResolver.resolve(WorkDefaultResDto.class))
                .additionalModels(typeResolver.resolve(TeamAbstractResDto.class))
                .additionalModels(typeResolver.resolve(TeamDefaultResDto.class))
                .additionalModels(typeResolver.resolve(TeamFavoriteResDto.class))
                .additionalModels(typeResolver.resolve(TeamMemberPositionResDto.class))
                .additionalModels(typeResolver.resolve(TeamMemberCntResDto.class))
                .additionalModels(typeResolver.resolve(OfferDefaultResDto.class))
                .additionalModels(typeResolver.resolve(ReviewDefaultResDto.class))
                .additionalModels(typeResolver.resolve(AdminAbstractResDto.class))
                .additionalModels(typeResolver.resolve(AdminDefaultResDto.class))
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
                .description("\t가보자IT ALPHA TEST SERVER\n\n" +
                        "<API 테스트를 위한 테스트 계정 Token 발급 방법>\n" +
                        "1. 개발\n" +
                        "2. 테스트 계정 토큰 발급\n" +
                        "3. tester-id = 1 ~ 100 (1 ~ 50 = 팀 리더 & 51 ~ 100 = 팀 없음)\n\n" +
                        "<요청시 Access Token 만료로 인한 401 응답을 받을 경우 재요청 방법>\n" +
                        "1. 요청을 보내고 401 응답을 받는다.\n" +
                        "2. 로그인 또는 회원 가입시 발급 받은 Refresh Token을 헤더에 \"Refresh-Token\": \"Bearer XXXXX\"와 같이 " +
                        "담아 토큰 재발급을 받는다.\n" +
                        "3. 재발급 받은 Access Token과 Refresh Token을 저장하고 새로운 Access Token으로 처음 보내려는 요청을 보낸다.\n" +
                        "- Refresh Token으로 토큰 재발급시 Refresh Token 만료로 인한 401 응답을 받을시 로그아웃 처리를 한다.\n\n" +
                        "<문서>\n" +
                        "1. 개인정보처리방침 -> /docs/privacy\n" +
                        "2. 서비스이용약관 -> /docs/service")
                .build();
    }
}
