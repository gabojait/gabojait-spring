package com.inuappcenter.gabojaitspring.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inuappcenter.gabojaitspring.exception.http.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final String tokenPrefix = "Bearer ";

    private final String secret;

    /**
     * 내부 필터 작동 |
     *
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("IN PROGRESS | 내부 필터 작동 At" + LocalDateTime.now() + " | request = " + request.toString());
        if (request.getServletPath().equals("/auth/signIn") || request.getServletPath().equals("/auth/refresh")) {
            log.info("COMPLETE | 내부 필터 작동 At" + LocalDateTime.now() +
                    " | request servlet path = " + request.getServletPath());
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith(tokenPrefix)) {
                String token = authorizationHeader.substring(tokenPrefix.length());
                try {
                    Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority(role));
                    });
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.info("COMPLETE | 내부 필터 작동 At" + LocalDateTime.now() + " | username = " + username);
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, String> body = new HashMap<>();

                    body.put("responseCode", "UNAUTHORIZED");
                    body.put("responseMessage", "인증에 실패했습니다");

                    response.setStatus(401);
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().println(objectMapper.writeValueAsString(body));
                    log.info("ERROR | 내부 필터 작동 At" + LocalDateTime.now() + " | " + body);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
