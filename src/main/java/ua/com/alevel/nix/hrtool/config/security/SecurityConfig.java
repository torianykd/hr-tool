package ua.com.alevel.nix.hrtool.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
                    .anyRequest().authenticated()
                .and()
                    .cors()
                .and()
                    .oauth2ResourceServer()
                    .jwt()
                    .jwtAuthenticationConverter(customJwtAuthenticationConverter);
    }
}
