package com.inuappcenter.gabojaitspring.project.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResponseDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Position;
import com.inuappcenter.gabojaitspring.profile.domain.Profile;
import com.inuappcenter.gabojaitspring.profile.service.ProfileService;
import com.inuappcenter.gabojaitspring.project.domain.Apply;
import com.inuappcenter.gabojaitspring.project.domain.Project;
import com.inuappcenter.gabojaitspring.project.dto.ApplyDefaultResponseDto;
import com.inuappcenter.gabojaitspring.project.dto.ApplySaveRequestDto;
import com.inuappcenter.gabojaitspring.project.service.ApplyService;
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
import java.util.List;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.TOKEN_AUTHORIZATION_FAIL;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "지원")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project/apply")
public class ApplyController {

    private final ApplyService applyService;
    private final ProjectService projectService;
    private final UserService userService;
    private final ProfileService profileService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "지원하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "지원하기 완료",
                    content = @Content(schema = @Schema(implementation = ApplyDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "사용자 에러"),
            @ApiResponse(responseCode = "401", description = "토큰 에러"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 정보"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResponseDto<Object>> apply(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid ApplySaveRequestDto request) {
        List<String> tokenInfo = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION));

        if (!tokenInfo.get(1).equals(JwtType.ACCESS.name())) {
            throw new CustomException(TOKEN_AUTHORIZATION_FAIL);
        }

        User user = userService.findOneByUsername(tokenInfo.get(0));
        Profile profile = profileService.findOne(user.getProfileId());
        Project project = projectService.findOne(new ObjectId(request.getProjectId()));
        Position position = profileService.validatePosition(request.getPosition());

        projectService.validatePositionAvailability(project, position);
        profileService.validateNoCurrentProject(profile);

        Apply apply = applyService.save(request, profile, position);
        projectService.projectApply(project, apply);

        ApplyDefaultResponseDto response = new ApplyDefaultResponseDto(apply);
        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("APPLY_COMPLETE")
                        .responseMessage("지원하기 완료")
                        .data(response)
                        .build());
    }


}
