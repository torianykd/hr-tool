package ua.com.alevel.nix.hrtool.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import ua.com.alevel.nix.hrtool.Routes;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    public SecurityConfig(CustomJwtAuthenticationConverter customJwtAuthenticationConverter) {
        this.customJwtAuthenticationConverter = customJwtAuthenticationConverter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                    .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                    // Employee
                    .mvcMatchers(HttpMethod.GET, Routes.EMPLOYEES).hasAuthority("SCOPE_read:resources")
                    .mvcMatchers(HttpMethod.PUT, Routes.LEAVE_REQUESTS + "/{id:\\d+}").hasAuthority("SCOPE_create:requests")
                    .mvcMatchers(HttpMethod.DELETE, Routes.LEAVE_REQUESTS + "/{id:\\d+}").hasAuthority("SCOPE_create:requests")
                    // Admin
                    .mvcMatchers(
                            Routes.DEPARTMENTS + "/**",
                            Routes.POSITIONS + "/**",
                            Routes.EMPLOYEES + "/**",
                            Routes.LEAVE_REQUESTS + "/{id:\\d+}/**",
                            Routes.LEAVE_REQUESTS + "/manage"
                    ).hasAuthority("SCOPE_manage:resources")
                .anyRequest().authenticated()
                .and()
                    .cors()
                .and()
                    .oauth2ResourceServer()
                    .jwt()
                    .jwtAuthenticationConverter(customJwtAuthenticationConverter);
    }
}
