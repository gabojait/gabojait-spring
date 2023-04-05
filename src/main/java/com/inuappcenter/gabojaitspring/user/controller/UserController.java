package com.inuappcenter.gabojaitspring.user.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.type.Position;
import com.inuappcenter.gabojaitspring.team.domain.Team;
import com.inuappcenter.gabojaitspring.team.dto.res.TeamAbstractResDto;
import com.inuappcenter.gabojaitspring.team.dto.res.TeamDefaultResDto;
import com.inuappcenter.gabojaitspring.team.service.TeamService;
import com.inuappcenter.gabojaitspring.user.domain.Contact;
import com.inuappcenter.gabojaitspring.user.domain.type.Gender;
import com.inuappcenter.gabojaitspring.user.domain.type.Role;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.dto.req.*;
import com.inuappcenter.gabojaitspring.user.dto.res.UserDefaultResDto;
import com.inuappcenter.gabojaitspring.user.service.ContactService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import static com.inuappcenter.gabojaitspring.common.SuccessCode.*;
import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "회원")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final ContactService contactService;
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final TeamService teamService;

    @ApiOperation(value = "아이디 중복여부 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NO_DUPLICATE_USERNAME",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400",
                    description = "USERNAME_LENGTH_INVALID / USERNAME_FORMAT_INVALID"),
            @ApiResponse(responseCode = "409", description = "EXISTING_USERNAME")
    })
    @GetMapping("/username/duplicate/{username}")
    public ResponseEntity<DefaultResDto<Object>> duplicateUsername(
            @PathVariable
            @Size(min = 5, max = 15, message = "아이디는 5~15자만 가능합니다.")
            @Pattern(regexp = "^(?=.*[A-z0-9])[A-z0-9]+$", message = "아이디는 영문과 숫자의 형식만 가능합니다.")
            String username) {

        userService.isExistingUsername(username);

        return ResponseEntity.status(USERNAME_NO_DUPLICATE.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(USERNAME_NO_DUPLICATE.name())
                        .responseMessage(USERNAME_NO_DUPLICATE.getMessage())
                        .build());
    }

    @ApiOperation(value = "가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "USER_REGISTERED",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "FIELD_REQUIRED / *_LENGTH_INVALID / *_FORMAT_INVALID / PASSWORD_MATCH_INVALID"),
            @ApiResponse(responseCode = "409",
                    description = "EXISTING_USERNAME / EXISTING_NICKNAME / EMAIL_VERIFICATION_INVALID"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<DefaultResDto<Object>> create(@RequestBody @Valid UserSaveReqDto request) {

        userService.isExistingUsername(request.getUsername());
        userService.isExistingNickname(request.getNickname());
        Gender gender = Gender.fromString(request.getGender());
        String encodedPassword = userService.validatePwAndPwReEnterAndEncode(
                request.getPassword(),
                request.getPasswordReEntered()
        );


        Contact contact = contactService.findOneUnregisteredByEmail(request.getEmail());
        contactService.register(contact);

        User user = userService.create(request, encodedPassword, gender, contact);

        String newTokens = jwtProvider.generateJwt(String.valueOf(user.getId()), user.getRoles());

        UserDefaultResDto responseBody = new UserDefaultResDto(user);

        return ResponseEntity.status(USER_REGISTERED.getHttpStatus())
                .header(AUTHORIZATION, newTokens)
                .body(DefaultResDto.SingleDataBuilder()
                        .responseCode(USER_REGISTERED.name())
                        .responseMessage(USER_REGISTERED.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "닉네임 중복여부 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NO_DUPLICATE_NICKNAME",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400",
                    description = "NICKNAME_LENGTH_INVALID / NICKNAME_PATTERN_INVALID"),
            @ApiResponse(responseCode = "409", description = "EXISTING_NICKNAME")
    })
    @GetMapping("/nickname/duplicate/{nickname}")
    public ResponseEntity<DefaultResDto<Object>> duplicateNickname(
            @PathVariable
            @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.")
            @Pattern(regexp = "^[가-힣]+$", message = "닉네임은 한글 형식만 가능합니다.")
            String nickname
    ) {
        userService.isExistingUsername(nickname);

        return ResponseEntity.status(NICKNAME_NO_DUPLICATE.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(NICKNAME_NO_DUPLICATE.name())
                        .responseMessage(NICKNAME_NO_DUPLICATE.getMessage())
                        .build());
    }

    @ApiOperation(value = "닉네임 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "NICKNAME_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "NICKNAME_LENGTH_INVALID / " +
                    "NICKNAME_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "EXISTING_NICKNAME"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/nickname/update/{nickname}")
    public ResponseEntity<DefaultResDto<Object>> updateNickname(
            HttpServletRequest servletRequest,
            @PathVariable
            @Size(min = 2, max = 8, message = "닉네임은 2~8자만 가능합니다.")
            @Pattern(regexp = "^[가-힣]+$", message = "닉네임은 한글 형식만 가능합니다.")
            String nickname
    ) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        userService.isExistingNickname(nickname);

        userService.updateNickname(user, nickname);

        return ResponseEntity.status(NICKNAME_UPDATED.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(NICKNAME_UPDATED.name())
                        .responseMessage(NICKNAME_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USER_LOGGED_IN / PASSWORD_FORCE_UPDATE",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "LOGIN_FAIL")
    })
    @PostMapping("/login")
    public ResponseEntity<DefaultResDto<Object>> login(@RequestBody @Valid UserLoginReqDto request) {

        User user = userService.login(request);

        List<String> authorities = new ArrayList<>(user.getRoles());

        String newTokens = jwtProvider.generateJwt(user.getId().toString(), authorities);

        UserDefaultResDto responseBody = new UserDefaultResDto(user);

        if (user.getIsTemporaryPassword()) {

            return ResponseEntity.status(PASSWORD_FORCE_UPDATE.getHttpStatus())
                    .body(DefaultResDto.SingleDataBuilder()
                            .responseCode(PASSWORD_FORCE_UPDATE.name())
                            .responseMessage(PASSWORD_FORCE_UPDATE.getMessage())
                            .data(responseBody)
                            .build());
        } else {

            return ResponseEntity.status(USER_LOGGED_IN.getHttpStatus())
                    .header(AUTHORIZATION, newTokens)
                    .body(DefaultResDto.SingleDataBuilder()
                            .responseCode(USER_LOGGED_IN.name())
                            .responseMessage(USER_LOGGED_IN.getMessage())
                            .data(responseBody)
                            .build());
        }
    }

    @ApiOperation(value = "본인 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MY_USER_FOUND",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping
    public ResponseEntity<DefaultResDto<Object>> findMyself(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));

        UserDefaultResDto responseBody = new UserDefaultResDto(user);

        return ResponseEntity.status(MY_USER_FOUND.getHttpStatus())
                .body(DefaultResDto.SingleDataBuilder()
                        .responseCode(MY_USER_FOUND.name())
                        .responseMessage(MY_USER_FOUND.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "단건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USER_FOUND",
                    content = @Content(schema = @Schema(implementation = UserDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/find/{user-id}")
    public ResponseEntity<DefaultResDto<Object>> findOne(HttpServletRequest servletRequest,
                                                         @PathVariable(value = "user-id") String userId) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        userService.findOneByUserId(token.get(0));

        User user = userService.findOneByUserId(userId);

        UserDefaultResDto responseBody = new UserDefaultResDto(user);

        return ResponseEntity.status(USER_FOUND.getHttpStatus())
                .body(DefaultResDto.SingleDataBuilder()
                        .responseCode(USER_FOUND.getMessage())
                        .responseMessage(USER_FOUND.getMessage())
                        .data(responseBody)
                        .build());
    }

    @ApiOperation(value = "토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TOKEN_RENEWED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PostMapping("/token/renew")
    public ResponseEntity<DefaultResDto<Object>> renewToken(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.REFRESH.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        List<String> authorities = new ArrayList<>(List.of(Role.USER.name()));
        String newTokens = jwtProvider.generateJwt(String.valueOf(user.getId()), authorities);

        return ResponseEntity.status(USER_TOKEN_RENEWED.getHttpStatus())
                .header(AUTHORIZATION, newTokens)
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(USER_TOKEN_RENEWED.name())
                        .responseMessage(USER_TOKEN_RENEWED.getMessage())
                        .build());
    }

    @ApiOperation(value = "아이디 찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USERNAME_EMAIL_SENT",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / EMAIL_FORMAT_INVALID"),
            @ApiResponse(responseCode = "404", description = "EMAIL_NOT_FOUND / USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR / MAIL_SENDING_ERROR")
    })
    @PostMapping("/username/find")
    public ResponseEntity<DefaultResDto<Object>> forgotUsername(@RequestBody @Valid
                                                                UserForgotUsernameReqDto request) {

        Contact contact = contactService.findOneRegisteredByEmail(request.getEmail());
        User user = userService.findOneByContact(contact);

        userService.sendUsernameEmail(user);

        return ResponseEntity.status(USERNAME_EMAIL_SENT.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(USERNAME_EMAIL_SENT.name())
                        .responseMessage(USERNAME_EMAIL_SENT.getMessage())
                        .build());
    }

    @ApiOperation(value = "비밀번호 찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PASSWORD_EMAIL_SENT",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / EMAIL_FORMAT_INVALID"),
            @ApiResponse(responseCode = "401", description = "USERNAME_EMAIL_NO_MATCH"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / EMAIL_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR / MAIL_SENDING_ERROR")
    })
    @PostMapping("/password/find")
    public ResponseEntity<DefaultResDto<Object>> forgotPassword(@RequestBody @Valid
                                                                UserForgotPasswordReqDto request) {

        User user = userService.findOneByUsername(request.getUsername());
        Contact contact = contactService.findOneRegisteredByEmail(request.getEmail());

        userService.resetPasswordAndSendEmail(user, contact.getEmail());

        return ResponseEntity.status(PASSWORD_EMAIL_SENT.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(PASSWORD_EMAIL_SENT.name())
                        .responseMessage(PASSWORD_EMAIL_SENT.getMessage())
                        .build());
    }

    @ApiOperation(value = "비밀번호 검증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PASSWORD_VERIFIED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PostMapping("/password/verify")
    public ResponseEntity<DefaultResDto<Object>> verifyPassword(
            HttpServletRequest servletRequest,
            @RequestBody @Valid UserVerifyPasswordReqDto request
    ) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        userService.validatePassword(user.getPassword(), request.getPassword());

        return ResponseEntity.status(PASSWORD_VERIFIED.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(PASSWORD_VERIFIED.name())
                        .responseMessage(PASSWORD_VERIFIED.getMessage())
                        .build());
    }

    @ApiOperation(value = "비밀번호 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PASSWORD_UPDATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "FIELD_REQUIRED / PASSWORD_LENGTH_INVALID / " +
                    "PASSWORD_MATCH_INVALID"),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR / MAIL_SENDING_ERROR")
    })
    @PatchMapping("/password")
    public ResponseEntity<DefaultResDto<Object>> updatePassword(
            HttpServletRequest servletRequest,
            @RequestBody @Valid UserUpdatePasswordReqDto request
    ) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        String encodedPassword = userService.validatePwAndPwReEnterAndEncode(
                request.getNewPassword(),
                request.getNewPasswordReEntered()
        );

        userService.updatePassword(user, encodedPassword, request.getIsTemporaryPassword());

        return ResponseEntity.status(PASSWORD_UPDATED.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(PASSWORD_UPDATED.name())
                        .responseMessage(PASSWORD_UPDATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "USER_DEACTIVATED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @DeleteMapping
    public ResponseEntity<DefaultResDto<Object>> deactivate(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);
        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);

        User user = userService.findOneByUserId(token.get(0));

        userService.deactivate(user);
        contactService.deactivate(user.getContact());
        // TODO: Must deactivate Profile, etc. after implementation.

        return ResponseEntity.status(USER_DEACTIVATED.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(USER_DEACTIVATED.name())
                        .responseMessage(USER_DEACTIVATED.getMessage())
                        .build());
    }

    @ApiOperation(value = "본인 팀 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MY_TEAM_FOUND / NOT_IN_TEAM",
                    content = @Content(schema = @Schema(implementation = TeamDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/team")
    public ResponseEntity<DefaultResDto<Object>> findMyTeam(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));

        if (user.getCurrentTeamId() == null) {
            return ResponseEntity.status(NOT_IN_TEAM.getHttpStatus())
                    .body(DefaultResDto.NoDataBuilder()
                            .responseCode(NOT_IN_TEAM.name())
                            .responseMessage(NOT_IN_TEAM.getMessage())
                            .build());
        } else {
            Team team = teamService.findOne(user.getCurrentTeamId().toString());

            TeamDefaultResDto responseBody = new TeamDefaultResDto(team);

            return ResponseEntity.status(MY_TEAM_FOUND.getHttpStatus())
                    .body(DefaultResDto.SingleDataBuilder()
                            .responseCode(MY_TEAM_FOUND.name())
                            .responseMessage(MY_TEAM_FOUND.getMessage())
                            .data(responseBody)
                            .build());
        }
    }

    @ApiOperation(value = "팀 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_LEFT",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "409", description = "TEAM_LEADER_CONFLICT"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/team/leave")
    public ResponseEntity<DefaultResDto<Object>> leaveTeam(HttpServletRequest servletRequest) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));
        userService.isNonExistingCurrentTeam(user);
        Team team = teamService.findOne(user.getCurrentTeamId().toString());
        teamService.validateNonLeader(team, user);
        Position position = teamService.getPositionInCurrentTeam(team, user);

        teamService.leaveTeam(team, user, position);

        return ResponseEntity.status(TEAM_LEFT.getHttpStatus())
                .body(DefaultResDto.NoDataBuilder()
                        .responseCode(TEAM_LEFT.name())
                        .responseMessage(TEAM_LEFT.getMessage())
                        .build());
    }

    @ApiOperation(value = "팀 찜하기 추가 / 제거")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAM_FAVORITE_ADDED / TEAM_FAVORITE_REMOVED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @PatchMapping("/team/{team-id}/favorite")
    public ResponseEntity<DefaultResDto<Object>> addOrRemoveFavoriteTeam(HttpServletRequest servletRequest,
                                                                 @PathVariable(value = "team-id") String teamId,
                                                                 @RequestBody @Valid
                                                                 UserTeamFavoriteDefaultReqDto request) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));

        if (user.getCurrentTeamId() != null) {
            Team team = teamService.findOne(user.getCurrentTeamId().toString());

            if (teamService.isLeader(team, user))
                throw new CustomException(ROLE_NOT_ALLOWED);
        }

        if (request.getIsAdd()) {

            Team team = teamService.findOne(teamId);

            userService.addFavoriteTeam(user, team.getId());

            return ResponseEntity.status(TEAM_FAVORITE_ADDED.getHttpStatus())
                    .body(DefaultResDto.NoDataBuilder()
                            .responseCode(TEAM_FAVORITE_ADDED.name())
                            .responseMessage(TEAM_FAVORITE_ADDED.getMessage())
                            .build());
        } else {

            userService.removeFavoriteTeam(user, new ObjectId(teamId));

            return ResponseEntity.status(TEAM_FAVORITE_REMOVED.getHttpStatus())
                    .body(DefaultResDto.NoDataBuilder()
                            .responseCode(TEAM_FAVORITE_REMOVED.name())
                            .responseMessage(TEAM_FAVORITE_REMOVED.getMessage())
                            .build());
        }
    }

    @ApiOperation(value = "팀 찜한 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FOUND_FAVORITE_TEAMS / ZERO_FAVORITE_TEAM",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED / ROLE_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND / TEAM_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/team/favorites")
    public ResponseEntity<DefaultResDto<Object>> findFavoriteTeams(HttpServletRequest servletRequest,
                                                                   @RequestParam Integer pageFrom,
                                                                   @RequestParam(required = false) Integer pageSize) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        User user = userService.findOneByUserId(token.get(0));

        if (user.getCurrentTeamId() != null) {
            Team team = teamService.findOne(user.getCurrentTeamId().toString());

            if (teamService.isLeader(team, user))
                throw new CustomException(ROLE_NOT_ALLOWED);
        }

        List<Team> teams = teamService.findManyUserFavoriteTeamsAndRemoveIfDeleted(user, pageFrom, pageSize);

        if (teams.isEmpty()) {

            return ResponseEntity.status(ZERO_FAVORITE_TEAM.getHttpStatus())
                    .body(DefaultResDto.MultiDataBuilder()
                            .responseCode(ZERO_FAVORITE_TEAM.name())
                            .responseMessage(ZERO_FAVORITE_TEAM.getMessage())
                            .data(null)
                            .size(user.getFavoriteTeamIds().size())
                            .build());
        } else {

            List<TeamAbstractResDto> responseBodies = new ArrayList<>();
            for (Team team : teams)
                responseBodies.add(new TeamAbstractResDto(team));

            return ResponseEntity.status(FOUND_FAVORITE_TEAMS.getHttpStatus())
                    .body(DefaultResDto.MultiDataBuilder()
                            .responseCode(FOUND_FAVORITE_TEAMS.name())
                            .responseMessage(FOUND_FAVORITE_TEAMS.getMessage())
                            .data(responseBodies)
                            .size(user.getFavoriteTeamIds().size())
                            .build());
        }
    }
}
