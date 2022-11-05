package com.inuappcenter.gabojaitspring.profile.controller;

import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.profile.dto.*;
import com.inuappcenter.gabojaitspring.profile.service.ProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @ApiResponse(code = 403, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "정보 조회 실패"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("/new")
    public ResponseEntity<Object> create(@RequestBody @Valid ProfileSaveRequestDto request) {
        ProfileDefaultResponseDto response = profileService.save(request);
        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("CREATED")
                        .responseMessage("프로필 저장 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "프로필 수정")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "프로필 수정 성공"),
            @ApiResponse(code = 400, message = "프로필 입력 에러"),
            @ApiResponse(code = 403, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "정보 조회 실패"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping
    public ResponseEntity<Object> update(@RequestBody @Valid ProfileUpdateRequestDto request) {
        ProfileDefaultResponseDto response = profileService.update(request);
        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("프로필 수정 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "프로필 조회")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "프로필 조회 성공"),
            @ApiResponse(code = 403, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "정보 조회 실패")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> findOneProfile(@PathVariable String id) {
        ProfileDefaultResponseDto response = profileService.findOneProfile(id);
        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("프로필 조회 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "학력 저장")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "학력 저장 성공"),
            @ApiResponse(code = 400, message = "프로필 학력 입력 에러"),
            @ApiResponse(code = 403, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "정보 조회 실패"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PostMapping("/education/new")
    public ResponseEntity<Object> createEducation(@RequestBody @Valid EducationSaveRequestDto request) {
        profileService.saveEducation(request);
        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("CREATED")
                        .responseMessage("학력 저장 완료")
                        .build());
    }

    @ApiOperation(value = "학력 수정")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "학력 수정 성공"),
            @ApiResponse(code = 400, message = "프로필 학력 입력 에러"),
            @ApiResponse(code = 403, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "정보 조회 실패"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping("/education")
    public ResponseEntity<Object> updateEducation(@RequestBody @Valid EducationUpdateRequestDto request) {
        profileService.updateEducation(request);
        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("OK")
                        .responseMessage("학력 수정 완료")
                        .build());
    }

    @ApiOperation(value = "학력 삭제")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "학력 삭제 성공"),
            @ApiResponse(code = 400, message = "프로필 학력 입력 에러"),
            @ApiResponse(code = 403, message = "토큰 인증 실패"),
            @ApiResponse(code = 404, message = "정보 조회 실패"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping("/education/delete")
    public ResponseEntity<Object> deleteEducation(@RequestBody @Valid EducationDeleteRequestDto request) {
        profileService.deleteEducation(request);
        return ResponseEntity.status(204)
                .body(DefaultResponseDto.builder()
                        .responseCode("NO_CONTENT")
                        .responseMessage("학력 삭제 완료")
                        .build());
    }
}
