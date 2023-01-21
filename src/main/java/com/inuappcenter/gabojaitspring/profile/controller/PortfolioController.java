package com.inuappcenter.gabojaitspring.profile.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Portfolio;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.dto.*;
import com.inuappcenter.gabojaitspring.profile.service.PortfolioService;
import com.inuappcenter.gabojaitspring.profile.service.ProfileService;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.TOKEN_AUTHORIZATION_FAIL;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Api(tags = "포트폴리오")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/profile/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final ProfileService profileService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "링크 포트폴리오 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "링크 포트폴리오 생성 완료",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원 또는 프로필"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/link")
    public ResponseEntity<DefaultResponseDto<Object>> createLink(HttpServletRequest servletRequest,
                                                             @RequestBody @Valid
                                                             PortfolioLinkSaveRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());

        Portfolio portfolio = portfolioService.saveLink(request, profile);
        profile = profileService.savePortfolio(profile, portfolio);

        ProfileDefaultResponseDto response = new ProfileDefaultResponseDto(profile);

        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("LINK_PORTFOLIO_CREATED")
                        .responseMessage("링크 포트폴리오 생성 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "파일 포트폴리오 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "파일 포트폴리오 생성 완료",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원 또는 프로필"),
            @ApiResponse(responseCode = "415", description = "미지원 파일 규격"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DefaultResponseDto<Object>> createFile(HttpServletRequest servletRequest,
                                                                 @ModelAttribute @Valid
                                                                 PortfolioFileSaveRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());

        Portfolio portfolio = portfolioService.saveFile(request, user.getUsername(), profile);
        profile = profileService.savePortfolio(profile, portfolio);

        ProfileDefaultResponseDto response = new ProfileDefaultResponseDto(profile);

        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("FILE_PORTFOLIO_CREATED")
                        .responseMessage("파일 포트폴리오 생성 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "링크 포트폴리오 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "링크 수정 완료",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원 또는 프로필"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PutMapping("/link")
    public ResponseEntity<DefaultResponseDto<Object>> updateLink(HttpServletRequest servletRequest,
                                                             @RequestBody @Valid
                                                             PortfolioLinkUpdateRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());

        portfolioService.updateLink(profile, request);
        profile = profileService.findOne(user.getProfileId());

        ProfileDefaultResponseDto response = new ProfileDefaultResponseDto(profile);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("LINK_PORTFOLIO_UPDATED")
                        .responseMessage("링크 포트폴리오 수정 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "파일 포트폴리오 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 수정 완료",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원 또는 프로필"),
            @ApiResponse(responseCode = "415", description = "미지원 파일 규격"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping(value = "/file/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<DefaultResponseDto<Object>> updateFile(HttpServletRequest servletRequest,
                                                          @ModelAttribute @Valid
                                                          PortfolioFileUpdateRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());

        portfolioService.updateFile(request, user.getUsername(), profile);
        profile = profileService.findOne(user.getProfileId());

        ProfileDefaultResponseDto response = new ProfileDefaultResponseDto(profile);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("FILE_PORTFOLIO_UPDATED")
                        .responseMessage("파일 포트폴리오 수정 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포트폴리오 삭제 완료",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원 또는 프로필"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{portfolio-id}")
    public ResponseEntity<DefaultResponseDto<Object>> delete(HttpServletRequest servletRequest,
                                                             @PathVariable(value = "portfolio-id")
                                                             @NotBlank(message = "포트폴리오 식별자를 입력해 주세요.")
                                                             String portfolioId) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());

        Portfolio portfolio = portfolioService.delete(profile, portfolioId);
        profile = profileService.deletePortfolio(profile, portfolio);

        ProfileDefaultResponseDto response = new ProfileDefaultResponseDto(profile);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("PORTFOLIO_DELETED")
                        .responseMessage("포트폴리오 삭제 완료")
                        .data(response)
                        .build());
    }
}
