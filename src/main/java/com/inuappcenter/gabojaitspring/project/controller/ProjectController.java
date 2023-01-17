package com.inuappcenter.gabojaitspring.project.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.service.ProfileService;
import com.inuappcenter.gabojaitspring.project.domain.Project;
import com.inuappcenter.gabojaitspring.project.dto.ProjectDefaultResponseDto;
import com.inuappcenter.gabojaitspring.project.dto.ProjectEndRequestDto;
import com.inuappcenter.gabojaitspring.project.dto.ProjectSaveRequestDto;
import com.inuappcenter.gabojaitspring.project.dto.ProjectStartRequestDto;
import com.inuappcenter.gabojaitspring.project.service.ProjectService;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.TOKEN_AUTHORIZATION_FAIL;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "프로젝트")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;
    private final ProfileService profileService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "새 프로젝트 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 완료",
                    content = @Content(schema = @Schema(implementation = ProjectDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 정보"),
            @ApiResponse(responseCode = "409", description = "이미 진행중인 프로젝트 존재"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResponseDto<Object>> start(HttpServletRequest servletRequest,
                                                             @RequestBody @Valid ProjectSaveRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());

        Project project = projectService.save(request, profile);
        profileService.startProject(profile, project);

        ProjectDefaultResponseDto response = new ProjectDefaultResponseDto(project,
                profile,
                null,
                null,
                null,
                null);

        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("PROJECT_STARTED")
                        .responseMessage("프로젝트 시작 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "프로젝트 시작")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "시작 완료"),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "403", description = "리더가 아닌 사용자 권한 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 정보"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping("/start")
    public ResponseEntity<DefaultResponseDto<Object>> start(HttpServletRequest servletRequest,
                                                          @RequestBody @Valid ProjectStartRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());
        ObjectId projectId = new ObjectId(request.getProjectId());
        Project project = projectService.findOne(projectId);

        projectService.start(project, profile);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("PROJECT_STARTED")
                        .responseMessage("프로젝트 시작 완료")
                        .build());
    }

    @ApiOperation(value = "프로젝트 종료")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "종료 완료"),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "403", description = "리더가 아닌 사용자 권한 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 정보"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping("/end")
    public ResponseEntity<DefaultResponseDto<Object>> end(HttpServletRequest servletRequest,
                                                          @RequestBody @Valid ProjectEndRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());
        ObjectId projectId = new ObjectId(request.getProjectId());
        Project project = projectService.findOne(projectId);

        project = projectService.end(project, profile);

        List<ObjectId> profileIds = new ArrayList<>();
        profileIds.addAll(project.getBackendProfileIds());
        profileIds.addAll(project.getFrontendProfileIds());
        profileIds.addAll(project.getDesignerProfileIds());
        profileIds.addAll(project.getManagerProfileIds());
        for (ObjectId profileId : profileIds) {
            Profile projectMemberProfile = profileService.findOne(profileId);
            profileService.endProject(projectMemberProfile, project);
        }

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("PROJECT_ENDED")
                        .responseMessage("프로젝트 종료 완료")
                        .build());
    }
}
