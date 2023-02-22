package com.inuappcenter.gabojaitspring.team.controller;

import com.inuappcenter.gabojaitspring.auth.JwtProvider;
import com.inuappcenter.gabojaitspring.auth.JwtType;
import com.inuappcenter.gabojaitspring.common.DefaultResDto;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.profile.domain.Position;
import com.inuappcenter.gabojaitspring.profile.dto.res.UserAbstractDefaultResDto;
import com.inuappcenter.gabojaitspring.profile.dto.res.UserProfileDefaultResDto;
import com.inuappcenter.gabojaitspring.user.domain.User;
import com.inuappcenter.gabojaitspring.user.domain.type.Role;
import com.inuappcenter.gabojaitspring.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.inuappcenter.gabojaitspring.common.SuccessCode.*;
import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "팀")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
public class TeamController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @ApiOperation(value = "팀원 찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TEAMMATES_FOUND",
                    content = @Content(schema = @Schema(implementation = UserProfileDefaultResDto.class))),
            @ApiResponse(responseCode = "401", description = " TOKEN_AUTHENTICATION_FAIL / TOKEN_REQUIRED_FAIL"),
            @ApiResponse(responseCode = "403", description = "TOKEN_NOT_ALLOWED"),
            @ApiResponse(responseCode = "404", description = "USER_NOT_FOUND"),
            @ApiResponse(responseCode = "500", description = "SERVER_ERROR")
    })
    @GetMapping("/user/{position}")
    public ResponseEntity<DefaultResDto<Object>> findTeammates(HttpServletRequest servletRequest,
                                                               @PathVariable String position,
                                                               @RequestParam @NotNull Integer pageFrom,
                                                               @RequestParam(required = false) Integer pageNum) {

        List<String> token = jwtProvider.authorizeJwt(servletRequest.getHeader(AUTHORIZATION), Role.USER);

        if (!token.get(1).equals(JwtType.ACCESS.name()))
            throw new CustomException(TOKEN_NOT_ALLOWED);

        userService.findOneByUserId(token.get(0));

        Page<User> users = userService.findManyByPosition(Position.fromString(position), pageFrom, pageNum);

        if (users.getSize() == 0) {

            return ResponseEntity.status(TEAMMATES_ZERO.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(TEAMMATES_ZERO.name())
                            .responseMessage(TEAMMATES_ZERO.getMessage())
                            .totalPageNum(users.getTotalPages())
                            .build());
        } else {

            List<UserAbstractDefaultResDto> responseBodies = new ArrayList<>();
            for (User u : users)
                responseBodies.add(new UserAbstractDefaultResDto(u));

            return ResponseEntity.status(TEAMMATES_FOUND.getHttpStatus())
                    .body(DefaultResDto.builder()
                            .responseCode(TEAMMATES_FOUND.name())
                            .responseMessage(TEAMMATES_FOUND.getMessage())
                            .data(responseBodies)
                            .totalPageNum(users.getTotalPages())
                            .build());
        }
    }
}
