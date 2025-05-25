package com.si.admin_management.config;

import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@org.springframework.context.annotation.Configuration
public class KcAuthzInitConfig {
    @Value("${keycloak.server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Bean
    public AuthzClient authzClient() {
        Configuration config = new Configuration(
                serverUrl,
                realm,
                clientId,
                Collections.singletonMap("secret", clientSecret),
                null
        );
        return AuthzClient.create(config);
    }
}
