package com.gabojait.gabojaitspring.config;

import com.gabojait.gabojaitspring.auth.CustomAuthenticationEntryPoint;
import com.gabojait.gabojaitspring.auth.CustomAuthenticationFilter;
import com.gabojait.gabojaitspring.domain.user.Role;
import com.gabojait.gabojaitspring.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthenticationFilter customAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,
                        "/swagger-resources/**", "/v2/api-docs", "/api/**/docs/**/**",
                        "/docs/**", "/api/**/health", "/api/**/monitor", "/api/**/user/username",
                        "/api/**/user/nickname", "/api/**/test/user/**")
                .permitAll()
                .antMatchers(HttpMethod.POST,
                        "/api/**/contact", "/api/**/user", "/api/**/user/login", "/api/**/user/username",
                        "/api/**/user/password", "/api/**/admin")
                .permitAll()
                .antMatchers(HttpMethod.PATCH, "/api/**/contact")
                .permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/**/test")
                .permitAll()
                .antMatchers("/api/**/master", "/api/**/master/**", "/api/**/master/**/**",
                        "/api/**/master/**/**/**")
                .hasAuthority(Role.MASTER.name())
                .antMatchers("/api/**/admin", "/api/**/admin/**", "/api/**/admin/**/**",
                        "/api/**/admin/**/**/**")
                .hasAuthority(Role.ADMIN.name())
                .antMatchers("/api/**/user", "/api/**/user/**", "/api/**/user/**/**",
                        "/api/**/user/**/**/**", "/api/**/team", "/api/**/team/**", "/api/**/team/**/**",
                        "/api/**/team/**/**/**")
                .hasAuthority(Role.USER.name())
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint);
    }
}
