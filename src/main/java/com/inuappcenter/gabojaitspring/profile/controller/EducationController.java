package com.inuappcenter.gabojaitspring.profile.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Education;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.dto.EducationSaveRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.EducationUpdateRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.ProfileDefaultResponseDto;
import com.inuappcenter.gabojaitspring.profile.dto.ProfileUpdateRequestDto;
import com.inuappcenter.gabojaitspring.profile.service.EducationService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.TOKEN_AUTHORIZATION_FAIL;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "학력")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/profile/education")
public class EducationController {

    private final EducationService educationService;
    private final ProfileService profileService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 완료",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원 또는 프로필"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResponseDto<Object>> create(HttpServletRequest servletRequest,
                                                             @RequestBody @Valid EducationSaveRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());

        Education education = educationService.save(request, profile);
        profile = profileService.saveEducation(profile, education);

        ProfileDefaultResponseDto response = new ProfileDefaultResponseDto(profile);

        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("EDUCATION_CREATED")
                        .responseMessage("학력 생성 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 완료",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 정보"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PutMapping
    public ResponseEntity<DefaultResponseDto<Object>> update(HttpServletRequest servletRequest,
                                                             @RequestBody @Valid EducationUpdateRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());

        educationService.update(profile, request);
        profile = profileService.findOne(user.getProfileId());

        ProfileDefaultResponseDto response = new ProfileDefaultResponseDto(profile);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("EDUCATION_UPDATED")
                        .responseMessage("학력 수정 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 완료",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 정보"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{educationId}")
    public ResponseEntity<DefaultResponseDto<Object>> delete(HttpServletRequest servletRequest,
                                                             @PathVariable
                                                             @NotBlank(message = "학력 식별자를 입력해 주세요.")
                                                             String educationId) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());

        Education education = educationService.delete(profile, educationId);
        profile = profileService.deleteEducation(profile, education);

        ProfileDefaultResponseDto response = new ProfileDefaultResponseDto(profile);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("EDUCATION_DELETED")
                        .responseMessage("학력 삭제 완료")
                        .data(response)
                        .build());
    }

}
