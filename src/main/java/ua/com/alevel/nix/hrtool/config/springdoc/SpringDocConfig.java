package ua.com.alevel.nix.hrtool.config.springdoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("auth0",
                        new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows().authorizationCode(
                                        new OAuthFlow().authorizationUrl(authUrl).tokenUrl(tokenUrl))
                                )
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("auth0"))
                .info(new Info().title("HR Tool API"));
    }
}
