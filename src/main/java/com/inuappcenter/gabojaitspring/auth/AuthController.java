package com.inuappcenter.gabojaitspring.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inuappcenter.gabojaitspring.exception.http.UnauthorizedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Api(tags = "Auth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final CustomUserDetailService customUserDetailService;
    @Value("secret")
    private String secret;

    private final String tokenPrefix = "Bearer ";

    @ApiOperation(value = "Auth 토큰 재발급")
    @PostMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(tokenPrefix)) {
            String refreshToken = authorizationHeader.substring(tokenPrefix.length());
            try {
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();

                UserDetails user = customUserDetailService.loadUserByUsername(username);

                String newAccessToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles",
                                user.getAuthorities()
                                        .stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.toList()))
                        .sign(algorithm);

                String newRefreshToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 28L * 24 * 60 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .sign(algorithm);

                Cookie accessTokenCookie = new Cookie("ACCESS-TOKEN", newAccessToken);
                Cookie refreshTokenCookie = new Cookie("REFRESH-TOKEN", newRefreshToken);

                accessTokenCookie.setMaxAge(60);
                refreshTokenCookie.setMaxAge(60);
//                accessTokenCookie.setSecure(true);
//                refreshTokenCookie.setSecure(true);

                response.addCookie(accessTokenCookie);
                response.addCookie(refreshTokenCookie);
            } catch (Exception e) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> body = new HashMap<>();

                body.put("responseCode", "UNAUTHORIZED");
                body.put("responseMessage", "인증에 실패했습니다");

                response.setStatus(401);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().println(objectMapper.writeValueAsString(body));
            }
        } else {
            throw new UnauthorizedException("Refresh 토큰이 없습니다");
        }
    }
}
