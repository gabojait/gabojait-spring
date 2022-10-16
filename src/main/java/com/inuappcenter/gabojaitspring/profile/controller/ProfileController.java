package com.inuappcenter.gabojaitspring.profile.controller;

import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.profile.dto.ProfileDefaultRequestDto;
import com.inuappcenter.gabojaitspring.profile.service.ProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "Profile")
@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    @ApiOperation(value = "프로필 저장")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "프로필 저장 성공"),
            @ApiResponse(code = 400, message = "프로필 입력 에러"),
            @ApiResponse(code = 403, message = "토큰 검증 실패"),
            @ApiResponse(code = 404, message = "유저 조회 실패"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("/new")
    public ResponseEntity<Object> create(@RequestBody @Valid ProfileDefaultRequestDto request) {
        profileService.save(request);
        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("CREATED")
                        .responseMessage("프로필 저장 완료")
                        .build());
    }
}
