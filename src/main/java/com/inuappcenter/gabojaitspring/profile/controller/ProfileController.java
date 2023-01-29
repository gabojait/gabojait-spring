package com.inuappcenter.gabojaitspring.profile.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.*;
import com.inuappcenter.gabojaitspring.profile.dto.req.PortfolioFileDefaultReqDto;
import com.inuappcenter.gabojaitspring.profile.dto.req.SkillDefaultReqDto;
import com.inuappcenter.gabojaitspring.profile.dto.req.WorkDefaultReqDto;
import com.inuappcenter.gabojaitspring.profile.service.PortfolioService;
import com.inuappcenter.gabojaitspring.profile.service.SkillService;
import com.inuappcenter.gabojaitspring.profile.service.WorkService;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.domain.type.Role;
import com.inuappcenter.gabojaitspring.profile.dto.req.EducationDefaultReqDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.inuappcenter.gabojaitspring.common.SuccessCode.*;
import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.TOKEN_AUTHENTICATION_FAIL;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "프로필")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class ProfileController {

    private final EductionService educationService;
    private final WorkService workService;
    private final SkillService skillService;
    private final PortfolioService portfolioService;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    @ApiOperation(value = "학력 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "EDUCATION_CREATED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
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

        Education education = educationService.saveEducation(user.getId(), request);
        userService.addEducation(user, education);

        UserProfileDefaultResDto response = new UserProfileDefaultResDto(user);

        return ResponseEntity.status(EDUCATION_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(EDUCATION_CREATED.name())
                        .responseMessage(EDUCATION_CREATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "학력 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "EDUCATION_UPDATED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / INISTITUTIONNAME_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / EDUCATION_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PutMapping("/education/update/{education-id}")
    public ResponseEntity<DefaultResDto<Object>> updateEducation(HttpServletRequest servletRequest,
                                                                 @PathVariable(value = "education-id")
                                                                 @NotBlank(message = "모든 필수 정보를 입력해주세요.")
                                                                 String educationId,
                                                                 @RequestBody @Valid EducationDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        Education education = educationService.findOneEducation(educationId);
        educationService.updateEducation(education, request);

        UserProfileDefaultResDto response = new UserProfileDefaultResDto(user);

        return ResponseEntity.status(EDUCATION_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(EDUCATION_UPDATED.name())
                        .responseMessage(EDUCATION_UPDATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "학력 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "EDUCATION_DELETED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / EDUCATION_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @DeleteMapping("/education/{education-id}")
    public ResponseEntity<DefaultResDto<Object>> deleteEducation(HttpServletRequest servletRequest,
                                                                 @PathVariable(value = "education-id")
                                                                 @NotBlank(message = "모든 필수 정보를 입력해주세요.")
                                                                 String educationId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        Education education = educationService.findOneEducation(educationId);

        userService.removeEducation(user, education);
        educationService.deleteEducation(education);

        UserProfileDefaultResDto response = new UserProfileDefaultResDto(user);

        return ResponseEntity.status(EDUCATION_DELETED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(EDUCATION_DELETED.name())
                        .responseMessage(EDUCATION_DELETED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "경력 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "WORK_CREATED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
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

        Work work = workService.saveWork(user.getId(), request);
        userService.addWork(user, work);

        UserProfileDefaultResDto response = new UserProfileDefaultResDto(user);

        return ResponseEntity.status(WORK_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(WORK_CREATED.name())
                        .responseCode(WORK_UPDATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "경력 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "WORK_UPDATED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / *_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / WORK_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PutMapping("/work/update/{work-id}")
    public ResponseEntity<DefaultResDto<Object>> updateWork(HttpServletRequest servletRequest,
                                                            @PathVariable(value = "work-id")
                                                            @NotBlank(message = "모든 필수 정보를 입력해주세요.")
                                                            String workId,
                                                            @RequestBody @Valid WorkDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Work work = workService.findOneWork(workId);

        workService.updateWork(work, request);

        UserProfileDefaultResDto response = new UserProfileDefaultResDto(user);

        return ResponseEntity.status(WORK_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(WORK_UPDATED.name())
                        .responseMessage(WORK_UPDATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "경력 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "WORK_DELETED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / WORK_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @DeleteMapping("/work/{work-id}")
    public ResponseEntity<DefaultResDto<Object>> deleteWork(HttpServletRequest servletRequest,
                                                            @PathVariable(value = "work-id")
                                                            @NotBlank(message = "모든 필수 정보를 입력해주세요.")
                                                            String workId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Work work = workService.findOneWork(workId);

        userService.removeWork(user, work);
        workService.deleteWork(work);

        UserProfileDefaultResDto response = new UserProfileDefaultResDto(user);

        return ResponseEntity.status(WORK_DELETED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(WORK_DELETED.name())
                        .responseMessage(WORK_DELETED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "기술 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "SKILL_CREATED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
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

        Level level = skillService.validateLevel(request.getLevel().byteValue());

        Skill skill = skillService.saveSkill(user.getId(), request, level);
        userService.addSkill(user, skill);

        UserProfileDefaultResDto response = new UserProfileDefaultResDto(user);

        return ResponseEntity.status(SKILL_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(SKILL_CREATED.name())
                        .responseMessage(SKILL_CREATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "기술 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SKILL_UPDATED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / SKILLNAME_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / SKILL_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/skill/update/{skill-id}")
    public ResponseEntity<DefaultResDto<Object>> updateSkill(HttpServletRequest servletRequest,
                                                             @PathVariable(value = "skill-id")
                                                             @NotBlank(message = "모든 필수 정보를 입력해주세요.")
                                                             String skillId,
                                                             @RequestBody @Valid SkillDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Skill skill = skillService.findOneSkill(skillId);

        Level level = skillService.validateLevel(request.getLevel().byteValue());

        skillService.updateSkill(skill, request, level);

        UserProfileDefaultResDto response = new UserProfileDefaultResDto(user);

        return ResponseEntity.status(SKILL_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(SKILL_UPDATED.name())
                        .responseMessage(SKILL_UPDATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "기술 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SKILL_DELETED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / SKILL_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @DeleteMapping("/skill/{skill-id}")
    public ResponseEntity<DefaultResDto<Object>> deleteSkill(HttpServletRequest servletRequest,
                                                             @PathVariable(value = "skill-id")
                                                             @NotBlank(message = "모든 필수 정보를 입력해주세요.")
                                                             String skillId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Skill skill = skillService.findOneSkill(skillId);

        userService.removeSkill(user, skill);
        skillService.deleteSkill(skill);

        UserProfileDefaultResDto response = new UserProfileDefaultResDto(user);

        return ResponseEntity.status(SKILL_DELETED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(SKILL_DELETED.name())
                        .responseMessage(SKILL_DELETED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "포트폴리오 파일 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "PORTFOLIO_FILE_CREATED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / PORTFOLIONAME_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / WORK_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/portfolio/file")
    public ResponseEntity<DefaultResDto<Object>> createPortfolioFile(HttpServletRequest servletRequest,
                                                                     @ModelAttribute @Valid
                                                                     PortfolioFileDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        Portfolio portfolio = portfolioService.savePortfolioFile(user.getId(), user.getUsername(), request);
        userService.addPortfolio(user, portfolio);

        UserProfileDefaultResDto response = new UserProfileDefaultResDto(user);

        return ResponseEntity.status(PORTFOLIO_FILE_CREATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PORTFOLIO_FILE_CREATED.name())
                        .responseMessage(PORTFOLIO_FILE_CREATED.name())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "포트폴리오 파일 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PORTFOLIO_FILE_UPDATED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / PORTFOLIONAME_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / PORTFOLIO_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PutMapping("/portfolio/file/update/{portfolio-id}")
    public ResponseEntity<DefaultResDto<Object>> updatePortfolioFile(HttpServletRequest servletRequest,
                                                                     @PathVariable(value = "portfolio-id")
                                                                     @NotBlank(message = "모든 필수 정보를 입력해주세요.")
                                                                     String portfolioId,
                                                                     @ModelAttribute @Valid
                                                                     PortfolioFileDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Portfolio portfolio = portfolioService.findOnePortfolio(portfolioId);

        portfolioService.updatePortfolioFile(user.getId(), user.getUsername(), portfolio, request);

        UserProfileDefaultResDto response = new UserProfileDefaultResDto(user);

        return ResponseEntity.status(PORTFOLIO_FILE_UPDATED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PORTFOLIO_FILE_UPDATED.name())
                        .responseMessage(PORTFOLIO_FILE_UPDATED.getMessage())
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "포트폴리오 파일 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PORTFOLIO_FILE_DELETED",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / PORTFOLIO_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @DeleteMapping("/portfolio/file/{portfolio-id}")
    public ResponseEntity<DefaultResDto<Object>> deletePortfolio(HttpServletRequest servletRequest,
                                                                 @PathVariable(value = "portfolio-id")
                                                                 @NotBlank(message = "모든 필수 정보를 입력해주세요.")
                                                                 String portfolioId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));
        Portfolio portfolio = portfolioService.findOnePortfolio(portfolioId);

        portfolioService.deletePortfolio(portfolio);
        userService.removePortfolio(user, portfolio);

        UserProfileDefaultResDto response = new UserProfileDefaultResDto(user);

        return ResponseEntity.status(PORTFOLIO_FILE_DELETED.getHttpStatus())
                .body(DefaultResDto.builder()
                        .responseCode(PORTFOLIO_FILE_DELETED.name())
                        .responseMessage(PORTFOLIO_FILE_DELETED.getMessage())
                        .data(response)
                        .build());
    }
}
