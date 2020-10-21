package ua.com.alevel.nix.hrtool.config.springdoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Value("${springdoc.oAuthFlow.authorizationUrl}")
    private String authUrl;

    @Value("${springdoc.oAuthFlow.tokenUrl}")
    private String tokenUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        OAuthFlow oAuthFlow = new OAuthFlow()
                .authorizationUrl(authUrl)
                .tokenUrl(tokenUrl)
                .scopes(new Scopes()
                        .addString("manage:resources", "Manage Resources Data")
                        .addString("read:resources", "Read Employees Data")
                        .addString("create:requests", "Create Leave Requests")
                );

        SecurityScheme oauth0Scheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows().authorizationCode(oAuthFlow));

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("auth0", oauth0Scheme))
                .addSecurityItem(new SecurityRequirement().addList("auth0"))
                .info(new Info().title("HR Tool API"));
    }
}
