package com.gabojait.gabojaitspring.offer.controller;

import com.gabojait.gabojaitspring.auth.JwtProvider;
import com.gabojait.gabojaitspring.common.dto.DefaultResDto;
import com.gabojait.gabojaitspring.offer.dto.req.OfferDefaultReqDto;
import com.gabojait.gabojaitspring.offer.service.OfferService;
import com.gabojait.gabojaitspring.team.service.TeamService;
import com.gabojait.gabojaitspring.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "제안")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OfferController {

    private final OfferService offerService;
    private final UserService userService;
    private final TeamService teamService;
    private final JwtProvider jwtProvider;

//    @ApiOperation(value = "회원이 팀 지원")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "OFFERED_TO_TEAM",
//                    content = @Content(schema = @Schema(implementation = Object.class)))
//    })
//    @PostMapping("/user/team/{team-id}/offer")
//    public ResponseEntity<DefaultResDto<Object>> userOffer(HttpServletRequest servletRequest,
//                                                           @PathVariable(value = "team-id")
//                                                           String teamId,
//                                                           @RequestBody @Valid OfferDefaultReqDto request) {
//
//    }
}
