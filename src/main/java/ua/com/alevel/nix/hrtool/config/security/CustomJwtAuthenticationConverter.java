package ua.com.alevel.nix.hrtool.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${auth0.emailClaim}")
    private String emailClaim;

    private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter
            = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
        return new JwtAuthenticationToken(jwt, authorities, jwt.getClaimAsString(emailClaim));
    }
}
