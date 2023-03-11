package com.inuappcenter.gabojaitspring.profile.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.*;
import com.inuappcenter.gabojaitspring.profile.domain.type.Level;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation(value = "포지션 선택", notes = "position = designer || backend || frontend || pm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "POSITION_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "POSITION_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/position/{position}")
    public ResponseEntity<DefaultResDto<Object>> selectPosition(HttpServletRequest servletRequest,
                                                                @PathVariable(value = "position")
                                                                String position) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        userService.selectPosition(user, Position.fromString(position));

        return ResponseEntity.status(POSITION_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(POSITION_UPDATED.name())
                        .responseMessage(POSITION_UPDATED.getMessage())
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

    @ApiOperation(value = "학력 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "EDUCATION_CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / INISTITUTIONNAME_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/education")
    public ResponseEntity<DefaultResDto<Object>> createEducation(HttpServletRequest servletRequest,
                                                                 @RequestBody @Valid EducationDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        Education education = educationService.save(request.toEntity(user.getId()));
        userService.addEducation(user, education);

        return ResponseEntity.status(EDUCATION_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(EDUCATION_CREATED.name())
                        .responseMessage(EDUCATION_CREATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "학력 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "EDUCATION_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / INISTITUTIONNAME_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / EDUCATION_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PutMapping("/education/{education-id}")
    public ResponseEntity<DefaultResDto<Object>> updateEducation(HttpServletRequest servletRequest,
                                                                 @PathVariable(value = "education-id")
                                                                 String educationId,
                                                                 @RequestBody @Valid EducationDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Education education = educationService.findOne(educationId);
        educationService.validateOwner(education, user);

        educationService.update(education, request);

        return ResponseEntity.status(EDUCATION_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(EDUCATION_UPDATED.name())
                        .responseMessage(EDUCATION_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "학력 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "EDUCATION_DELETED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / EDUCATION_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @DeleteMapping("/education/{education-id}")
    public ResponseEntity<DefaultResDto<Object>> deleteEducation(HttpServletRequest servletRequest,
                                                                 @PathVariable(value = "education-id")
                                                                 String educationId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Education education = educationService.findOne(educationId);
        educationService.validateOwner(education, user);

        userService.removeEducation(user, education);
        educationService.delete(education);

        return ResponseEntity.status(EDUCATION_DELETED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(EDUCATION_DELETED.name())
                        .responseMessage(EDUCATION_DELETED.getMessage())
                        .build());
    }

    @ApiOperation(value = "경력 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "WORK_CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / *_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/work")
    public ResponseEntity<DefaultResDto<Object>> createWork(HttpServletRequest servletRequest,
                                                            @RequestBody @Valid WorkDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        Work work = workService.save(request.toEntity(user.getId()));
        userService.addWork(user, work);

        return ResponseEntity.status(WORK_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(WORK_CREATED.name())
                        .responseCode(WORK_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "경력 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "WORK_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / *_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / WORK_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PutMapping("/work/{work-id}")
    public ResponseEntity<DefaultResDto<Object>> updateWork(HttpServletRequest servletRequest,
                                                            @PathVariable(value = "work-id")
                                                            String workId,
                                                            @RequestBody @Valid WorkDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Work work = workService.findOne(workId);
        workService.validateOwner(work, user);

        workService.update(work, request);

        return ResponseEntity.status(WORK_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(WORK_UPDATED.name())
                        .responseMessage(WORK_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "경력 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "WORK_DELETED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / WORK_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @DeleteMapping("/work/{work-id}")
    public ResponseEntity<DefaultResDto<Object>> deleteWork(HttpServletRequest servletRequest,
                                                            @PathVariable(value = "work-id")
                                                            String workId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Work work = workService.findOne(workId);
        workService.validateOwner(work, user);

        userService.removeWork(user, work);
        workService.delete(work);

        return ResponseEntity.status(WORK_DELETED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(WORK_DELETED.name())
                        .responseMessage(WORK_DELETED.getMessage())
                        .build());
    }

    @ApiOperation(value = "기술 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "SKILL_CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / SKILLNAME_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/skill")
    public ResponseEntity<DefaultResDto<Object>> createSkill(HttpServletRequest servletRequest,
                                                             @RequestBody @Valid SkillDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        Skill skill = skillService.save(request.toEntity(user.getId(), Level.fromString(request.getLevel())));
        userService.addSkill(user, skill);

        return ResponseEntity.status(SKILL_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(SKILL_CREATED.name())
                        .responseMessage(SKILL_CREATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "기술 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SKILL_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / SKILLNAME_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / SKILL_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PutMapping("/skill/{skill-id}")
    public ResponseEntity<DefaultResDto<Object>> updateSkill(HttpServletRequest servletRequest,
                                                             @PathVariable(value = "skill-id")
                                                             String skillId,
                                                             @RequestBody @Valid SkillDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Skill skill = skillService.findOne(skillId);
        skillService.validateOwner(skill, user);

        skillService.update(skill, request, Level.fromString(request.getLevel()));

        return ResponseEntity.status(SKILL_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(SKILL_UPDATED.name())
                        .responseMessage(SKILL_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "기술 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SKILL_DELETED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / SKILL_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @DeleteMapping("/skill/{skill-id}")
    public ResponseEntity<DefaultResDto<Object>> deleteSkill(HttpServletRequest servletRequest,
                                                             @PathVariable(value = "skill-id")
                                                             String skillId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Skill skill = skillService.findOne(skillId);
        skillService.validateOwner(skill, user);

        userService.removeSkill(user, skill);
        skillService.delete(skill);

        return ResponseEntity.status(SKILL_DELETED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(SKILL_DELETED.name())
                        .responseMessage(SKILL_DELETED.getMessage())
                        .build());
    }

    @ApiOperation(value = "파일 포트폴리오 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "PORTFOLIO_FILE_CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / PORTFOLIONAME_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/portfolio/file")
    public ResponseEntity<DefaultResDto<Object>> createPortfolioFile(HttpServletRequest servletRequest,
                                                                     @ModelAttribute @Valid
                                                                     PortfolioFileSaveReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        String url = portfolioService.uploadToS3(user.getId(), user.getUsername(), request.getFile());
        Portfolio portfolio = portfolioService.save(request.toEntity(user.getId(), url));
        userService.addPortfolio(user, portfolio);

        return ResponseEntity.status(PORTFOLIO_FILE_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PORTFOLIO_FILE_CREATED.name())
                        .responseMessage(PORTFOLIO_FILE_CREATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "파일 포트폴리오 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PORTFOLIO_FILE_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / PORTFOLIONAME_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / PORTFOLIO_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PostMapping("/portfolio/file/update")
    public ResponseEntity<DefaultResDto<Object>> updatePortfolioFile(HttpServletRequest servletRequest,
                                                                     @ModelAttribute @Valid
                                                                     PortfolioFileUpdateReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Portfolio portfolio = portfolioService.findOne(request.getPortfolioId());
        portfolioService.validateOwner(portfolio, user);

        String url = portfolioService.uploadToS3(user.getId(), user.getUsername(), request.getFile());
        portfolioService.update(portfolio, request.getPortfolioName(), url);

        return ResponseEntity.status(PORTFOLIO_FILE_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PORTFOLIO_FILE_UPDATED.name())
                        .responseMessage(PORTFOLIO_FILE_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "포트폴리오 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PORTFOLIO_DELETED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / PORTFOLIO_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @DeleteMapping("/portfolio/{portfolio-id}")
    public ResponseEntity<DefaultResDto<Object>> deletePortfolio(HttpServletRequest servletRequest,
                                                                 @PathVariable(value = "portfolio-id")
                                                                 String portfolioId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Portfolio portfolio = portfolioService.findOne(portfolioId);
        portfolioService.validateOwner(portfolio, user);

        portfolioService.delete(portfolio);
        userService.removePortfolio(user, portfolio);

        return ResponseEntity.status(PORTFOLIO_DELETED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PORTFOLIO_DELETED.name())
                        .responseMessage(PORTFOLIO_DELETED.getMessage())
                        .build());
    }

    @ApiOperation(value = "링크 포트폴리오 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "PORTFOLIO_LINK_CREATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / PORTFOLIONAME_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/portfolio/link")
    public ResponseEntity<DefaultResDto<Object>> createPortfolioLink(HttpServletRequest servletRequest,
                                                                     @RequestBody @Valid
                                                                     PortfolioLinkDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        Portfolio portfolio = portfolioService.save(request.toEntity(user.getId()));
        userService.addPortfolio(user, portfolio);

        return ResponseEntity.status(PORTFOLIO_LINK_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PORTFOLIO_LINK_CREATED.name())
                        .responseMessage(PORTFOLIO_LINK_CREATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "링크 포트폴리오 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PORTFOLIO_LINK_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / PORTFOLIONAME_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / PORTFOLIO_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PutMapping("/portfolio/link/{portfolio-id}")
    public ResponseEntity<DefaultResDto<Object>> updatePortfolioLink(HttpServletRequest servletRequest,
                                                                     @PathVariable(value = "portfolio-id")
                                                                     String portfolioId,
                                                                     @RequestBody @Valid
                                                                     PortfolioLinkDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Portfolio portfolio = portfolioService.findOne(portfolioId);
        portfolioService.validateOwner(portfolio, user);

        portfolioService.update(portfolio, request.getPortfolioName(), request.getUrl());

        return ResponseEntity.status(PORTFOLIO_LINK_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PORTFOLIO_LINK_UPDATED.name())
                        .responseMessage(PORTFOLIO_LINK_UPDATED.getMessage())
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
