package com.inuappcenter.gabojaitspring.profile.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.*;
import com.inuappcenter.gabojaitspring.profile.domain.type.Level;
import com.inuappcenter.gabojaitspring.profile.domain.type.PortfolioType;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.profile.dto.req.*;
import com.inuappcenter.gabojaitspring.profile.dto.res.UserProfileAbstractResDto;
import com.inuappcenter.gabojaitspring.profile.service.PortfolioService;
import com.inuappcenter.gabojaitspring.profile.service.SkillService;
import com.inuappcenter.gabojaitspring.profile.service.WorkService;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.service.TeamService;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.domain.type.Role;
import com.inuappcenter.gabojaitspring.profile.dto.res.UserProfileDefaultResDto;
import com.inuappcenter.gabojaitspring.profile.service.EductionService;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static com.inuappcenter.gabojaitspring.common.SuccessCode.*;
import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.TOKEN_AUTHENTICATION_FAIL;
import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.TOKEN_NOT_ALLOWED;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "프로필")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/profile")
public class ProfileController {

    private final EductionService educationService;
    private final WorkService workService;
    private final SkillService skillService;
    private final PortfolioService portfolioService;
    private final TeamService teamService;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    @ApiOperation(value = "본인 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MY_PROFILE_FOUND",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping
    public ResponseEntity<DefaultResDto<Object>> findMyself(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        List<Team> teams = teamService.findAllPrevious(user);

        UserProfileDefaultResDto responseBody = new UserProfileDefaultResDto(user, teams);

        return ResponseEntity.status(MY_PROFILE_FOUND.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(MY_PROFILE_FOUND.name())
                        .responseMessage(MY_PROFILE_FOUND.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "단건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROFILE_FOUND",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/find/{user-id}")
    public ResponseEntity<DefaultResDto<Object>> findOne(HttpServletRequest servletRequest,
                                                         @PathVariable(value = "user-id")
                                                         String userId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        userService.findOneByUserId(token.get(0));

        User user = userService.findOneByUserId(userId);
        List<Team> teams = teamService.findAllPrevious(user);

        UserProfileDefaultResDto responseBody = new UserProfileDefaultResDto(user, teams);

        return ResponseEntity.status(PROFILE_FOUND.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PROFILE_FOUND.name())
                        .responseMessage(PROFILE_FOUND.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "프로필 사진 업로드/수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROFILE_IMG_UPDATED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DefaultResDto<Object>> uploadProfileImage(HttpServletRequest servletRequest,
                                                                    @RequestPart(name = "image") MultipartFile image) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        List<Team> teams = teamService.findAllPrevious(user);

        String url = userService.uploadToS3(user.getId(), user.getUsername(), image);
        userService.updateImageUrl(user, url);

        UserProfileDefaultResDto responseBody = new UserProfileDefaultResDto(user, teams);

        return ResponseEntity.status(PROFILE_IMG_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PROFILE_IMG_UPDATED.name())
                        .responseMessage(PROFILE_IMG_UPDATED.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "프로필 사진 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROFILE_IMG_DELETED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @DeleteMapping(value = "/image")
    public ResponseEntity<DefaultResDto<Object>> deleteProfileImage(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        List<Team> teams = teamService.findAllPrevious(user);

        userService.updateImageUrl(user, null);

        UserProfileDefaultResDto responseBody = new UserProfileDefaultResDto(user, teams);

        return ResponseEntity.status(PROFILE_IMG_DELETED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PROFILE_IMG_DELETED.name())
                        .responseMessage(PROFILE_IMG_DELETED.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "공개 여부 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROFILE_VISIBILITY_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/visibility")
    public ResponseEntity<DefaultResDto<Object>> updateVisibility(HttpServletRequest servletRequest,
                                                                  @RequestBody @Valid
                                                                  UserProfileVisibilityUpdateReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        userService.updateIsPublic(user, request.getIsPublic());

        return ResponseEntity.status(PROFILE_VISIBILITY_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PROFILE_VISIBILITY_UPDATED.name())
                        .responseMessage(PROFILE_VISIBILITY_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "자기소개 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PROFILE_DESCRIPTION_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "DESCRIPTION_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/description")
    public ResponseEntity<DefaultResDto<Object>> updateDescription(HttpServletRequest servletRequest,
                                                                   @RequestBody @Valid
                                                                   UserProfileDescriptionDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        userService.updateDescription(user, request.getDescription());

        return ResponseEntity.status(PROFILE_DESCRIPTION_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PROFILE_DESCRIPTION_UPDATED.name())
                        .responseMessage(PROFILE_DESCRIPTION_UPDATED.getMessage())
                        .build());

    }

    @ApiOperation(value = "포지션/기술 생성, 수정, 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "POSITION_AND_SKILL_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / *_LENGTH_INVALID, *_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / SKILL_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PostMapping("/position-and-skill")
    public ResponseEntity<DefaultResDto<Object>> updatePositionAndSkill(HttpServletRequest servletRequest,
                                                                      @RequestBody @Valid
                                                                      PositionAndSkillDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        // Validation
        if (!request.getPosition().isBlank())
            Position.fromString(request.getPosition());
        for (SkillCreateReqDto createSkill : request.getCreateSkills())
            Level.fromString(createSkill.getLevel());
        for (SkillUpdateReqDto updateSkill : request.getUpdateSkills()) {
            Skill skill = skillService.findOne(updateSkill.getSkillId());
            Level.fromString(updateSkill.getLevel());
            skillService.validateOwner(skill, user);
        }
        for (String deleteSkill : request.getDeleteSkills()) {
            Skill skill = skillService.findOne(deleteSkill);
            skillService.validateOwner(skill, user);
        }

        // Create & Update & Delete
        if (request.getPosition().isBlank()) {
            userService.selectPosition(user, null);
        } else {
            userService.selectPosition(user, Position.fromString(request.getPosition()));
        }
        for (SkillCreateReqDto createSkill : request.getCreateSkills()) {
            Level level = Level.fromString(createSkill.getLevel());
            Skill skill = skillService.save(createSkill.toEntity(user.getId(), level));
            userService.addSkill(user, skill);
        }
        for (SkillUpdateReqDto updateSkill : request.getUpdateSkills()) {
            Level level = Level.fromString(updateSkill.getLevel());
            Skill skill = skillService.findOne(updateSkill.getSkillId());
            skillService.update(skill, updateSkill, level);
        }
        for (String deleteSkill : request.getDeleteSkills()) {
            Skill skill = skillService.findOne(deleteSkill);
            userService.removeSkill(user, skill);
            skillService.delete(skill);
        }

        return ResponseEntity.status(POSITION_AND_SKILL_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(POSITION_AND_SKILL_UPDATED.name())
                        .responseMessage(POSITION_AND_SKILL_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "학력/경력 생성, 수정, 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "EDUCATION_AND_WORK_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / *_LENGTH_INVALID / *_DATE_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / EDUCATION_NOT_FOUND / WORK_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PostMapping("/education-and-work")
    public ResponseEntity<DefaultResDto<Object>> updateEducationAndWork(HttpServletRequest servletRequest,
                                                                        @RequestBody @Valid
                                                                        EducationAndWorkDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        // Validation
        for (EducationCreateReqDto createEducation : request.getCreateEducations())
            educationService.validateDate(createEducation.getStartedDate(), createEducation.getEndedDate());
        for (EducationUpdateReqDto updateEducation : request.getUpdateEducations()) {
            Education education = educationService.findOne(updateEducation.getEducationId());
            educationService.validateDate(updateEducation.getStartedDate(), updateEducation.getEndedDate());
            educationService.validateOwner(education, user);
        }
        for (String deleteEducation : request.getDeleteEducations()) {
            Education education = educationService.findOne(deleteEducation);
            educationService.validateOwner(education, user);
        }
        for (WorkCreateReqDto createWork : request.getCreateWorks())
            workService.validateDate(createWork.getStartedDate(), createWork.getEndedDate());
        for (WorkUpdateReqDto updateWork : request.getUpdateWorks()) {
            Work work = workService.findOne(updateWork.getWorkId());
            workService.validateDate(updateWork.getStartedDate(), updateWork.getEndedDate());
            workService.validateOwner(work, user);
        }
        for (String deleteWork : request.getDeleteWorks()) {
            Work work = workService.findOne(deleteWork);
            workService.validateOwner(work, user);
        }

        // Create & Update & Delete
        for (EducationCreateReqDto createEducation : request.getCreateEducations()) {
            Education education = educationService.save(createEducation.toEntity(user.getId()));
            userService.addEducation(user, education);
        }
        for (EducationUpdateReqDto updateEducation : request.getUpdateEducations()) {
            Education education = educationService.findOne(updateEducation.getEducationId());
            educationService.update(education, updateEducation);
        }
        for (String deleteEducation: request.getDeleteEducations()) {
            Education education = educationService.findOne(deleteEducation);
            userService.removeEducation(user, education);
            educationService.delete(education);
        }
        for (WorkCreateReqDto createWork : request.getCreateWorks()) {
            Work work = workService.save(createWork.toEntity(user.getId()));
            userService.addWork(user, work);
        }
        for (WorkUpdateReqDto updateWork : request.getUpdateWorks()) {
            Work work = workService.findOne(updateWork.getWorkId());
            workService.update(work, updateWork);
        }
        for (String deleteWork : request.getDeleteWorks()) {
            Work work = workService.findOne(deleteWork);
            userService.removeWork(user, work);
            workService.delete(work);
        }

        return ResponseEntity.status(EDUCATION_AND_WORK_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(EDUCATION_AND_WORK_UPDATED.name())
                        .responseMessage(EDUCATION_AND_WORK_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "링크 포트폴리오 생성, 수정, 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LINK_PORTFOLIO_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / *_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / PORTFOLIO_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PostMapping("/portfolio/link")
    public ResponseEntity<DefaultResDto<Object>> updateLinkPortfolio(HttpServletRequest servletRequest,
                                                                     @RequestBody @Valid
                                                                     PortfolioLinkDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        // Validation
        for (PortfolioLinkUpdateReqDto updateLinkPortfolio : request.getUpdateLinkPortfolios()) {
            Portfolio portfolio = portfolioService.findOne(updateLinkPortfolio.getPortfolioId());
            portfolioService.validateOwner(portfolio, user);
        }
        for (String deletePortfolio : request.getDeletePortfolios()) {
            Portfolio portfolio = portfolioService.findOne(deletePortfolio);
            portfolioService.validateOwner(portfolio, user);
        }

        // Create & Update & Delete
        for (PortfolioLinkCreateReqDto createLinkPortfolio : request.getCreateLinkPortfolios()) {
            Portfolio portfolio = portfolioService.save(createLinkPortfolio.toEntity(user.getId()));
            userService.addPortfolio(user, portfolio);
        }
        for (PortfolioLinkUpdateReqDto updateLinkPortfolio : request.getUpdateLinkPortfolios()) {
            Portfolio portfolio = portfolioService.findOne(updateLinkPortfolio.getPortfolioId());
            portfolioService.update(portfolio, updateLinkPortfolio.getPortfolioName(), updateLinkPortfolio.getUrl());
        }
        for (String deletePortfolio : request.getDeletePortfolios()) {
            Portfolio portfolio = portfolioService.findOne(deletePortfolio);
            userService.removePortfolio(user, portfolio);
            portfolioService.delete(portfolio);
        }

        return ResponseEntity.status(LINK_PORTFOLIO_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(LINK_PORTFOLIO_UPDATED.name())
                        .responseMessage(LINK_PORTFOLIO_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "파일 포트폴리오 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "FILE_PORTFOLIO_CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / *_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "413", description = "FILE_SIZE_EXCEED"),
            @ApiResponse(responseCode = "415", description = "FILE_TYPE_UNSUPPORTED"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/portfolio/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DefaultResDto<Object>> createFilePortfolio(HttpServletRequest servletRequest,
                                                                     @ModelAttribute @Valid PortfolioFileDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        portfolioService.validateFileType(request.getFile());

        String url = portfolioService.uploadToS3(user.getId(), user.getUsername(), request.getFile());
        Portfolio portfolio = portfolioService.save(request.toEntity(user.getId(), url));
        userService.addPortfolio(user, portfolio);

        return ResponseEntity.status(FILE_PORTFOLIO_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(FILE_PORTFOLIO_CREATED.name())
                        .responseMessage(FILE_PORTFOLIO_CREATED.getMessage())
                        .data(Object.class)
                        .build());
    }

    @ApiOperation(value = "파일 포트폴리오 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FILE_PORTFOLIO_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / *_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / PORTFOLIO_NOT_FOUND"),
            @ApiResponse(responseCode = "413", description = "FILE_SIZE_EXCEED"),
            @ApiResponse(responseCode = "415", description = "FILE_TYPE_UNSUPPORTED"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PostMapping(value = "/portfolio/{portfolio-id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DefaultResDto<Object>> updateFilePortfolio(HttpServletRequest servletRequest,
                                                                     @PathVariable(value = "portfolio-id")
                                                                     String portfolioId,
                                                                     @ModelAttribute @Valid
                                                                     PortfolioFileDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Portfolio portfolio = portfolioService.findOne(portfolioId);

        portfolioService.validateFileType(request.getFile());
        portfolioService.validateOwner(portfolio, user);

        String url = portfolioService.uploadToS3(user.getId(), user.getUsername(), request.getFile());
        portfolioService.update(portfolio, request.getPortfolioName(), url);

        return ResponseEntity.status(FILE_PORTFOLIO_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(FILE_PORTFOLIO_UPDATED.name())
                        .responseMessage(FILE_PORTFOLIO_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀 구하는 유저 다건 조회", notes = "position = designer || backend || frontend || pm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAMMATES_FOUND / TEAMMATES_ZERO",
                    content = @Content(schema = @Schema(implementation = UserProfileAbstractResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/{position}")
    public ResponseEntity<DefaultResDto<Object>> findUsersLookingForTeam(HttpServletRequest servletRequest,
                                                               @PathVariable String position,
                                                               @RequestParam Integer pageFrom,
                                                               @RequestParam(required = false) Integer pageSize) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        userService.findOneByUserId(token.get(0));

        Page<User> users = userService.findManyByPosition(Position.fromString(position), pageFrom, pageSize);

        if (users.getNumberOfElements() == 0) {

            return ResponseEntity.status(TEAMMATES_ZERO.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(TEAMMATES_ZERO.name())
                            .responseMessage(TEAMMATES_ZERO.getMessage())
                            .totalPageSize(users.getTotalPages())
                            .build());
        } else {

            List<UserProfileAbstractResDto> responseBodies = new ArrayList<>();
            for (User u : users)
                responseBodies.add(new UserProfileAbstractResDto(u));

            return ResponseEntity.status(TEAMMATES_FOUND.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(TEAMMATES_FOUND.name())
                            .responseMessage(TEAMMATES_FOUND.getMessage())
                            .data(responseBodies)
                            .totalPageSize(users.getTotalPages())
                            .build());
        }
    }
}
