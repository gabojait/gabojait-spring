package com.gabojait.gabojaitspring.config;

import com.gabojait.gabojaitspring.auth.CustomAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,
                        "/swagger-resources/**", "/v2/api-docs", "/api/**/docs/swagger-ui/index.html",
                        "/docs/**", "/api/**/health", "/api/**/user/username", "/api/**/user/nickname",
                        "/api/**/test/user/**")
                .permitAll()
                .antMatchers(HttpMethod.POST,
                        "/api/**/contact", "/api/**/user", "/api/**/user/login", "/api/**/user/username",
                        "/api/**/user/password", "/api/**/admin", "/api/**/admin/login")
                .permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/**/test")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }
}
