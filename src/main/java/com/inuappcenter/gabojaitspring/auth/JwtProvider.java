package com.inuappcenter.gabojaitspring.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.inuappcenter.gabojaitspring.exception.CustomException;
import com.inuappcenter.gabojaitspring.user.domain.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.inuappcenter.gabojaitspring.exception.ExceptionCode.*;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.domain}")
    private String domain;

    private final String tokenPrefix = "Bearer ";
    private final long accessTokenTime = 30L * 60 * 1000;
    private final long refreshTokenTime = 28L * 60 * 60 * 1000;

    /**
     * JWT 제작
     */
    public String generateJwt(String userId, Collection<String> authorities) {
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));

        List<String> authorityList = new ArrayList<>(authorities);

        List<String> token = new ArrayList<>();
        long time = System.currentTimeMillis();

        token.add("access-token: ".concat(tokenGenerator(userId, time, accessTokenTime, authorityList, algorithm)));
        token.add("refresh-token: ".concat(tokenGenerator(userId, time, refreshTokenTime, authorityList, algorithm)));

        return String.valueOf(token);
    }

    /**
     * 토큰 제작
     */
    private String tokenGenerator(String memberId,
                                  long time,
                                  long tokenTime,
                                  List<String> authorities,
                                  Algorithm algorithm) {
        return JWT.create()
                .withSubject(memberId)
                .withIssuedAt(new Date(time))
                .withExpiresAt(new Date(time + tokenTime))
                .withIssuer(domain)
                .withClaim("roles", authorities)
                .sign(algorithm);
    }

    /**
     * JWT 인증
     */
    public void authenticateJwt(String token, Role role) {
        verifyJwt(token, role);
    }

    /**
     * JWT 인가
     */
    public List<String> authorizeJwt(String token, Role role) {

        return verifyJwt(token, role);
    }

    /**
     * JWT 검증
     * 401(TOKEN_REQUIRED_FAIL)
     * 401(TOKEN_AUTHENTICATION_FAIL)
     */
    public List<String> verifyJwt(String token, Role role) {

        if (token == null || token.equals(""))
            throw new CustomException(TOKEN_REQUIRED_FAIL);

        token = token.substring(tokenPrefix.length());
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
        JWTVerifier verifier = JWT.require(algorithm).build();
        List<String> verifiedInfo = new ArrayList<>();

        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

            if(role != null)
                if (!roles.contains(role.name()))
                    throw new CustomException(ROLE_NOT_ALLOWED);

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            roles.forEach(r -> {
                authorities.add(new SimpleGrantedAuthority(r));
            });

            Long validTime = decodedJWT.getExpiresAt().getTime() - decodedJWT.getIssuedAt().getTime();
            String type;
            if (validTime.equals(accessTokenTime)) {
                type = JwtType.ACCESS.name();
            } else if (validTime.equals(refreshTokenTime)) {
                type = JwtType.REFRESH.name();
            } else {
                throw new CustomException(TOKEN_AUTHENTICATION_FAIL);
            }

            String memberId = decodedJWT.getSubject();

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(memberId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            verifiedInfo.add(memberId);
            verifiedInfo.add(type);
        } catch (JWTVerificationException ignored) {
            throw new CustomException(TOKEN_AUTHENTICATION_FAIL);
        }

        return verifiedInfo;
    }
}
