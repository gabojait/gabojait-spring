package com.inuappcenter.gabojaitspring.project.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Position;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.service.ProfileService;
import com.inuappcenter.gabojaitspring.project.domain.Project;
import com.inuappcenter.gabojaitspring.project.domain.Recruit;
import com.inuappcenter.gabojaitspring.project.dto.RecruitAcceptOrDeclineRequestDto;
import com.inuappcenter.gabojaitspring.project.dto.RecruitDefaultResponseDto;
import com.inuappcenter.gabojaitspring.project.dto.RecruitSaveRequestDto;
import com.inuappcenter.gabojaitspring.project.service.ProjectService;
import com.inuappcenter.gabojaitspring.project.service.RecruitService;
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
import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.TOKEN_AUTHORIZATION_FAIL;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "영입")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project/recruit")
public class RecruitController {

    private final RecruitService recruitService;
    private final ProjectService projectService;
    private final UserService userService;
    private final ProfileService profileService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "영입하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "영입하기 완료",
                    content = @Content(schema = @Schema(implementation = RecruitDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 정보"),
            @ApiResponse(responseCode = "409", description = "비지니스 로직 에러"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResponseDto<Object>> recruit(HttpServletRequest servletRequest,
                                                              @RequestBody @Valid RecruitSaveRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile leaderProfile = profileService.findOne(user.getProfileId());
        Profile userProfile = profileService.findOne(new ObjectId(request.getUserProfileId()));
        Project project = projectService.findOne(leaderProfile.getCurrentProject().getId());

        Position position = profileService.validatePosition(request.getPosition());
        projectService.validatePositionAvailability(project, position.getType());
        profileService.validateNoCurrentProject(userProfile);
        projectService.validateLeader(project, leaderProfile);

        Recruit recruit = recruitService.save(request, project, position);

        RecruitDefaultResponseDto response = new RecruitDefaultResponseDto(recruit);
        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("RECRUIT_COMPLETE")
                        .responseMessage("영입하기 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "수락/거절")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수락/거절 완료",
                    content = @Content(schema = @Schema(implementation = RecruitDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 정보"),
            @ApiResponse(responseCode = "409", description = "비지니스 로직 에러"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PatchMapping
    public ResponseEntity<DefaultResponseDto<Object>> acceptOrDeclineRecruit(HttpServletRequest servletRequest,
                                                                             @RequestBody @Valid
                                                                             RecruitAcceptOrDeclineRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());
        Recruit recruit = recruitService.findOne(new ObjectId(request.getRecruitId()));
        Project project = projectService.findOne(recruit.getProjectId());

        projectService.validatePositionAvailability(project, recruit.getPosition());
        profileService.validateNoCurrentProject(profile);

        recruit = recruitService.acceptOrDecline(recruit, request.getIsAccepted());
        projectService.joinProject(project, profile.getId(), recruit.getPosition());

        RecruitDefaultResponseDto response = new RecruitDefaultResponseDto(recruit);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("RECRUIT_RESULT")
                        .responseMessage("영입 수락/거절 완료")
                        .data(response)
                        .build());
    }
}
