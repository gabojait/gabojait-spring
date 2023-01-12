package com.inuappcenter.gabojaitspring.profile.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.dto.ProfileDefaultResponseDto;
import com.inuappcenter.gabojaitspring.profile.dto.ProfileSaveRequestDto;
import com.inuappcenter.gabojaitspring.profile.dto.ProfileUpdateRequestDto;
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
import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "프로필")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "기본 프로필 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "기본 프로필 생성 완료",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 회원"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/new")
    public ResponseEntity<DefaultResponseDto<Object>> create(HttpServletRequest servletRequest,
                                                             @RequestBody @Valid ProfileSaveRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));

        Profile profile = profileService.save(request);
        userService.saveProfileId(user, profile.getId());
        profileService.saveUserId(user, profile);

        ProfileDefaultResponseDto response = new ProfileDefaultResponseDto(profile);

        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("DEFAULT_PROFILE_CREATED")
                        .responseMessage("기본 프로필 생성 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "프로필 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 완료",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 정보"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping
    public ResponseEntity<DefaultResponseDto<Object>> updateProfile(HttpServletRequest servletRequest,
                                                             @RequestBody @Valid ProfileUpdateRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());

        profile = profileService.updateProfile(profile, request);

        ProfileDefaultResponseDto response = new ProfileDefaultResponseDto(profile);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("PROFILE_UPDATED")
                        .responseMessage("프로필 수정 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "프로필 사진 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 사진 수정 완료",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 정보"),
            @ApiResponse(responseCode = "415", description = "미지원 파일 규격"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping(value = "/img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DefaultResponseDto<Object>> updateImage(HttpServletRequest servletRequest,
                                                                  @RequestPart(required = false) MultipartFile image) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());

        profileService.updateImage(user.getUsername(), profile, image);

        ProfileDefaultResponseDto response = new ProfileDefaultResponseDto(profile);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("PROFILE_IMAGE_UPDATED")
                        .responseMessage("프로필 사진 수정 완료")
                        .data(response)
                        .build());
    }
}
