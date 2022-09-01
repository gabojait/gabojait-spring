package com.inuappcenter.gabojaitspring.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final String secret;

    /**
     * 인증 시도 |
     * 유저의 아이디와 비밀번호를 사용해서 사용자 정보를 확인한 후 성공할 경우 인증 토큰을 발급하고 인증 성공(successfulAuthentication) 메소드를
     * 호출한다. 실패일 경우 인증 실패(unsuccessfulAuthentication) 메소드를 호출한다.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.info("IN PROGRESS | 인증 시도 At " + LocalDateTime.now() + " | " + request.toString());
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        log.info("COMPLETE | 인증 시도 At " + LocalDateTime.now() + " | " + authenticationToken);
        return authenticationManager.authenticate(authenticationToken);
    }

    /**
     * 인증 성공 |
     * 인증 성공시 access token과 refresh token을 응답 헤더에 포함합니다.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        log.info("IN PROGRESS | 인증 성공 At " + LocalDateTime.now() + " | request = " + request.toString());
        User user = (User) authentication.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));

        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",
                        user.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 28L * 24 * 60 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        response.addHeader("ACCESS-TOKEN", accessToken);
        response.addHeader("REFRESH-TOKEN", refreshToken);

        log.info("COMPLETE | 인증 성공 At " + LocalDateTime.now());
    }

    /**
     * 인증 실패 |
     * 인증 실패시
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        log.info("IN PROGRESS | 인증 실패 At " + LocalDateTime.now() + " | failed = " + failed.getMessage());

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> body = new HashMap<>();

        body.put("responseCode", "UNAUTHORIZED");
        body.put("responseMessage", "인증에 실패했습니다");

        response.setStatus(401);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().println(objectMapper.writeValueAsString(body));

    }
}
