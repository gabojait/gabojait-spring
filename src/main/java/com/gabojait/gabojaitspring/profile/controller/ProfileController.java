package com.gabojait.gabojaitspring.profile.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.profile.domain.Education;
import com.gabojait.gabojaitspring.profile.domain.Portfolio;
import com.gabojait.gabojaitspring.profile.domain.Skill;
import com.gabojait.gabojaitspring.profile.domain.Work;
import com.gabojait.gabojaitspring.profile.dto.req.*;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileAbstractResDto;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileDefaultResDto;
import com.gabojait.gabojaitspring.profile.dto.res.ProfileDetailResDto;
import com.gabojait.gabojaitspring.profile.service.EducationService;
import com.gabojait.gabojaitspring.profile.service.PortfolioService;
import com.gabojait.gabojaitspring.profile.service.SkillService;
import com.gabojait.gabojaitspring.profile.service.WorkService;
import com.gabojait.gabojaitspring.team.domain.Team;
import com.gabojait.gabojaitspring.team.dto.res.TeamAbstractResDto;
import com.gabojait.gabojaitspring.team.service.TeamService;
import com.gabojait.gabojaitspring.user.domain.User;
import com.gabojait.gabojaitspring.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

import static com.gabojait.gabojaitspring.common.code.SuccessCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "프로필")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class ProfileController {

    private final EducationService educationService;
    private final PortfolioService portfolioService;
    private final SkillService skillService;
    private final WorkService workService;
    private final UserService userService;
    private final TeamService teamService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "본인 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SELF_PROFILE_FOUND",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @GetMapping("/profile")
    public ResponseEntity<DefaultResDto<Object>> findMyself(HttpServletRequest servletRequest) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        List<Team> previousTeams = teamService.findAllCompleted(user);

        // response
        ProfileDefaultResDto response = new ProfileDefaultResDto(user, previousTeams);

        return ResponseEntity.status(SELF_PROFILE_FOUND.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(SELF_PROFILE_FOUND.name())
                        .responseMessage(SELF_PROFILE_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "단건 조회", notes = "* user-id = NotBlank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROFILE_FOUND",
                    content = @Content(schema = @Schema(implementation = ProfileDetailResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @GetMapping("/{user-id}/profile")
    public ResponseEntity<DefaultResDto<Object>> findOther(HttpServletRequest servletRequest,
                                                           @PathVariable(value = "user-id")
                                                           @NotBlank(message = "회원 식별자를 입력해 주세요.")
                                                           String userId) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        User otherUser = userService.findOther(user, userId);
        List<Team> previousTeams = teamService.findAllCompleted(otherUser);
        Boolean isFavorite = teamService.isFavoriteUser(user, otherUser);

        // response
        ProfileDetailResDto response = new ProfileDetailResDto(otherUser, previousTeams, isFavorite);

        return ResponseEntity.status(PROFILE_FOUND.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(PROFILE_FOUND.name())
                        .responseMessage(PROFILE_FOUND.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "프로필 사진 업로드 또는 수정", notes = "* image = NotNull")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROFILE_IMAGE_UPLOADED",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "FILE_FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "413", description = "FILE_SIZE_EXCEED"),
            @ApiResponse(responseCode = "415", description = "IMAGE_TYPE_UNSUPPORTED"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PostMapping(value = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DefaultResDto<Object>> uploadProfileImage(HttpServletRequest servletRequest,
                                                                    @RequestPart(value = "image")
                                                                    MultipartFile image) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        userService.uploadProfileImage(user, image);
        // sub
        List<Team> completedTeams = teamService.findAllCompleted(user);

        // response
        ProfileDefaultResDto response = new ProfileDefaultResDto(user, completedTeams);

        return ResponseEntity.status(PROFILE_IMAGE_UPLOADED.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(PROFILE_IMAGE_UPLOADED.name())
                        .responseMessage(PROFILE_IMAGE_UPLOADED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "프로필 사진 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROFILE_IMAGE_DELETED",
                    content = @Content(schema = @Schema(implementation = ProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @DeleteMapping("/profile/image")
    public ResponseEntity<DefaultResDto<Object>> deleteProfileImage(HttpServletRequest servletRequest) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        userService.deleteProfileImage(user);
        // sub
        List<Team> completedTeams = teamService.findAllCompleted(user);

        // response
        ProfileDefaultResDto response = new ProfileDefaultResDto(user, completedTeams);

        return ResponseEntity.status(PROFILE_IMAGE_DELETED.getHttpStatus())
                .body(DefaultResDto.singleDataBuilder()
                        .responseCode(PROFILE_IMAGE_DELETED.name())
                        .responseMessage(PROFILE_IMAGE_DELETED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "팀 찾기 여부 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROFILE_VISIBILITY_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "IS_PUBLIC_FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PatchMapping("/profile/is-seeking-team")
    public ResponseEntity<DefaultResDto<Object>> updateIsSeekingTeam(HttpServletRequest servletRequest,
                                                                  @RequestBody @Valid
                                                                  ProfileIsSeekingTeamUpdateReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        userService.updateIsSeekingTeam(user, request.getIsSeekingTeam());

        return ResponseEntity.status(PROFILE_VISIBILITY_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PROFILE_VISIBILITY_UPDATED.name())
                        .responseMessage(PROFILE_VISIBILITY_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "자기소개 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROFILE_DESCRIPTION_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "PROFILE_DESCRIPTION_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PatchMapping("/profile/description")
    public ResponseEntity<DefaultResDto<Object>> updateDescription(HttpServletRequest servletRequest,
                                                                   @RequestBody @Valid
                                                                   ProfileDescriptionUpdateReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        userService.updateProfileDescription(user, request.getProfileDescription());

        return ResponseEntity.status(PROFILE_DESCRIPTION_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(PROFILE_DESCRIPTION_UPDATED.name())
                        .responseMessage(PROFILE_DESCRIPTION_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "포지션과 기술 생성, 수정, 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "POSITION_AND_SKILL_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "*_FIELD_REQUIRED / *_LENGTH_INVALID / *_TYPE_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN / REQUEST_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "SKILL_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PostMapping("/profile/position-and-skill")
    public ResponseEntity<DefaultResDto<Object>> updatePositionAndSkill(HttpServletRequest servletRequest,
                                                                        @RequestBody @Valid
                                                                        PositionAndSkillDefaultReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        List<Skill> createdSkills = skillService.createAll(user.getId(), request.getCreateSkills());
        List<Skill> deletedSkills = skillService.deleteAll(user.getId(), request.getDeleteSkillIds());
        // main
        skillService.updateAll(user.getId(), request.getUpdateSkills());
        userService.updatePositionAndSkills(user, request.getPosition(), createdSkills, deletedSkills);

        return ResponseEntity.status(POSITION_AND_SKILL_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(POSITION_AND_SKILL_UPDATED.name())
                        .responseMessage(POSITION_AND_SKILL_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "학력과 경력 생성, 수정, 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "EDUCATION_AND_WORK_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "*_FIELD_REQUIRED / *_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN / REQUEST_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "EDUCATION_NOT_FOUND / WORK_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PostMapping("/profile/education-and-work")
    public ResponseEntity<DefaultResDto<Object>> updateEducationAndWork(HttpServletRequest servletRequest,
                                                                        @RequestBody @Valid
                                                                        EducationAndWorkDefaultReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        List<Education> createdEducations = educationService.createAll(user.getId(), request.getCreateEducations());
        List<Education> deletedEducations = educationService.deleteAll(user.getId(), request.getDeleteEducationIds());
        List<Work> createdWorks = workService.createAll(user.getId(), request.getCreateWorks());
        List<Work> deletedWorks = workService.deleteAll(user.getId(), request.getDeleteWorkIds());
        // main
        educationService.updateAll(user.getId(), request.getUpdateEducations());
        workService.updateAll(user.getId(), request.getUpdateWorks());
        userService.updateEducationsAndWorks(user, createdEducations, deletedEducations, createdWorks, deletedWorks);

        return ResponseEntity.status(EDUCATION_AND_WORK_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(EDUCATION_AND_WORK_UPDATED.name())
                        .responseMessage(EDUCATION_AND_WORK_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "링크 포트폴리오 생성, 수정, 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LINK_PORTFOLIO_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "*_FIELD_REQUIRED / *_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN / REQUEST_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "PORTFOLIO_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PostMapping("/portfolio/link")
    public ResponseEntity<DefaultResDto<Object>> updateLinkPortfolio(HttpServletRequest servletRequest,
                                                                     @RequestBody @Valid
                                                                     PortfolioLinkDefaultReqDto request) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        List<Portfolio> createdPortfolios = portfolioService.createLinkAll(user.getId(),
                request.getCreateLinkPortfolios());
        List<Portfolio> deletedPortfolios = portfolioService.deleteAll(user.getId(), request.getDeletePortfolioIds());
        // main
        portfolioService.updateLinkAll(user.getId(), request.getUpdateLinkPortfolios());
        userService.updatePortfolios(user, createdPortfolios, deletedPortfolios);

        return ResponseEntity.status(LINK_PORTFOLIO_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(LINK_PORTFOLIO_UPDATED.name())
                        .responseMessage(LINK_PORTFOLIO_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "파일 포트폴리오 생성, 수정, 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FILE_PORTFOLIO_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400",
                    description = "FILE_FIELD_REQUIRED / PORTFOLIO_NAME_LENGTH_INVALID / " +
                            "CREATE_PORTFOLIO_CNT_MATCH_INVALID / UPDATE_PORTFOLIO_CNT_MATCH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN / REQUEST_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "PORTFOLIO_NOT_FOUND"),
            @ApiResponse(responseCode = "413", description = "FILE_SIZE_EXCEED / FILE_COUNT_EXCEED"),
            @ApiResponse(responseCode = "415", description = "FILE_TYPE_UNSUPPORTED"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PostMapping(value = "/portfolio/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DefaultResDto<Object>> updateFilePortfolio(
            HttpServletRequest servletRequest,
            @RequestPart(value = "create-portfolio-names", required = false)
            List<String> createPortfolioNames,
            @RequestPart(name = "create-portfolio-files", required = false)
            List<MultipartFile> createPortfolioFiles,
            @RequestPart(value = "update-portfolio-ids", required = false)
            List<String> updatePortfolioIds,
            @RequestPart(value = "update-portfolio-names", required = false)
            List<String> updatePortfolioNames,
            @RequestPart(name = "update-portfolio-files", required = false)
            List<MultipartFile> updatePortfolioFiles,
            @RequestPart(value = "delete-portfolio-ids", required = false)
            List<String> deletePortfolioIds
    ) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        portfolioService.validateFilesPreAll(createPortfolioNames,
                createPortfolioFiles,
                updatePortfolioIds,
                updatePortfolioNames,
                updatePortfolioFiles,
                deletePortfolioIds);
        List<Portfolio> createdPortfolios = portfolioService.createFileAll(user,
                createPortfolioNames,
                createPortfolioFiles);
        List<Portfolio> deletedPortfolios = portfolioService.deleteAll(user.getId(), deletePortfolioIds);
        // main
        portfolioService.updateFileAll(user, updatePortfolioIds, updatePortfolioNames, updatePortfolioFiles);
        userService.updatePortfolios(user, createdPortfolios, deletedPortfolios);

        return ResponseEntity.status(FILE_PORTFOLIO_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(FILE_PORTFOLIO_UPDATED.name())
                        .responseMessage(FILE_PORTFOLIO_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀을 찾는 회원 다건 조회",
            notes = "* position = NotBlank && Pattern(regex = ^(designer|backend|frontend|manager|none))\n" +
                    "* profile-order = NotBlank && Pattern(regex = ^(active|popularity|rating))\n" +
                    "* page-from = NotNull && PositiveOrZero\n" +
                    "* page-size = Positive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USERS_FINDING_TEAM_FOUND",
                    content = @Content(schema = @Schema(implementation = ProfileAbstractResDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "*_FIELD_REQUIRED / *_TYPE_INVALID / PAGE_FROM_POSITIVE_OR_ZER_ONLY / " +
                            "PAGE_SIZE_POSITIVE_ONLY"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @GetMapping("/profile/finding-team")
    public ResponseEntity<DefaultResDto<Object>> findUsersLookingForTeam(
            HttpServletRequest servletRequest,
            @RequestParam(value = "position")
            @NotBlank(message = "포지션을 입력해 주세요.")
            @Pattern(regexp = "^(designer|backend|frontend|manager|none)",
                    message = "포지션은 'designer', 'backend', 'frontend', 'manager', 또는 'none' 중 하나여야 됩니다.")
            String position,
            @RequestParam(value = "profile-order")
            @NotBlank(message = "프로필 정렬 기준을 입력해 주세요.")
            @Pattern(regexp = "^(active|popularity|rating)",
                    message = "정렬 기준은 'active', 'popularity', 'rating' 중 하나여야 됩니다.")
            String profileOrder,
            @RequestParam(value = "page-from")
            @NotNull(message = "페이지 시작점을 입력해 주세요.")
            @PositiveOrZero(message = "페이지 시작점은 0 또는 양수만 가능합니다.")
            Integer pageFrom,
            @RequestParam(value = "page-size", required = false)
            @Positive(message = "페이지 사이즈는 양수만 가능합니다.")
            Integer pageSize) {
        // auth
        jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // main
        Page<User> users = userService.findPagePositionProfileOrder(position, profileOrder, pageFrom, pageSize);

        // response
        List<ProfileAbstractResDto> responses = new ArrayList<>();
        for (User user : users)
            responses.add(new ProfileAbstractResDto(user));

        return ResponseEntity.status(USERS_FINDING_TEAM_FOUND.getHttpStatus())
                .body(DefaultResDto.multiDataBuilder()
                        .responseCode(USERS_FINDING_TEAM_FOUND.name())
                        .responseMessage(USERS_FINDING_TEAM_FOUND.getMessage())
                        .data(responses)
                        .size(users.getTotalPages())
                        .build());
    }

    @ApiOperation(value = "회원의 팀 찜 업데이트", notes = "* team-id = NotBlank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_FAVORITE_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "TEAM_ID_FIELD_REQUIRED / IS_ADD_FAVORITE_FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "404", description = "TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @PatchMapping(value = "/team/{team-id}/favorite")
    public ResponseEntity<DefaultResDto<Object>> updateFavoriteTeam(
            HttpServletRequest servletRequest,
            @PathVariable(value = "team-id")
            @NotBlank(message = "팀 식별자를 입력해 주세요.")
            String teamId,
            @RequestBody
            @Valid
            ProfileFavoriteUpdateReqDto request
    ) {
        // auth
        User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

        // sub
        Team team = teamService.findOneId(teamId);
        // main
        userService.updateFavoriteTeam(user, team, request.getIsAddFavorite());

        return ResponseEntity.status(TEAM_FAVORITE_UPDATED.getHttpStatus())
                .body(DefaultResDto.noDataBuilder()
                        .responseCode(TEAM_FAVORITE_UPDATED.name())
                        .responseMessage(TEAM_FAVORITE_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "찜한 팀 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FAVORITE_TEAMS_FOUND",
                    content = @Content(schema = @Schema(implementation = TeamAbstractResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_FORBIDDEN"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR"),
            @ApiResponse(responseCode = "503", description = "ONGOING_INSPECTION")
    })
    @GetMapping("/team/favorite")
    public ResponseEntity<DefaultResDto<Object>> findAllFavoriteTeams(HttpServletRequest servletRequest) {
         // auth
         User user = jwtProvider.authorizeUserAccessJwt(servletRequest.getHeader(AUTHORIZATION));

         // main
         List<Team> teams = teamService.findAllId(user.getFavoriteTeamIds());

         // response
         List<TeamAbstractResDto> responses = new ArrayList<>();
         for (Team team : teams)
             responses.add(new TeamAbstractResDto(team));

         return ResponseEntity.status(FAVORITE_TEAMS_FOUND.getHttpStatus())
                 .body(DefaultResDto.multiDataBuilder()
                         .responseCode(FAVORITE_TEAMS_FOUND.name())
                         .responseMessage(FAVORITE_TEAMS_FOUND.getMessage())
                         .data(responses)
                         .size(responses.size() > 0 ? 1 : 0)
                         .build());
     }
}
