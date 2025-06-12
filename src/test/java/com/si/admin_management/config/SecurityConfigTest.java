package com.si.admin_management.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private RptTokenExchangeFilter rptTokenExchangeFilter;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpSecurity http;

    @Test
    void testSecurityFilterChain() throws Exception {
        SecurityFilterChain filterChain = securityConfig.securityFilterChain(http, rptTokenExchangeFilter);
        assertNotNull(filterChain);
        verify(http).build();
    }

    @Test
    void testJwtAuthenticationConverterCreation() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();
        assertNotNull(converter);
    }

    @Test
    void testJwtConverter_withAllClaims() {
        // GIVEN
        Jwt jwt = createJwt("read write",
                Map.of("roles", List.of("user", "admin")),
                Map.of("permissions", List.of(
                        Map.of("rsname", "product-api", "scopes", List.of("view", "edit")),
                        Map.of("rsname", "order-api", "scopes", List.of("view"))
                )));
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();

        // WHEN
        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        // THEN
        Set<String> authorityStrings = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        assertThat(authorityStrings).containsExactlyInAnyOrder(
                "SCOPE_read", "SCOPE_write",
                "ROLE_user", "ROLE_admin",
                "PERMISSION_product-api:view", "PERMISSION_product-api:edit", "PERMISSION_order-api:view"
        );
    }

    @Test
    void testJwtConverter_withOnlyScopes() {
        // GIVEN
        Jwt jwt = createJwt("read", null, null);
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();

        // WHEN
        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        // THEN
        Set<String> authorityStrings = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        assertThat(authorityStrings).containsExactly("SCOPE_read");
    }

    @Test
    void testJwtConverter_withRealmAccessButNoRolesKey() {
        // GIVEN
        Jwt jwt = createJwt("read", Map.of("some_other_key", "some_value"), null);
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();

        // WHEN
        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        // THEN
        Set<String> authorityStrings = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        assertThat(authorityStrings).containsExactly("SCOPE_read");
    }

    @Test
    void testJwtConverter_withAuthorizationButNoPermissionsKey() {
        // GIVEN
        Jwt jwt = createJwt("read", null, Map.of("some_other_key", "some_value"));
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();

        // WHEN
        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        // THEN
        Set<String> authorityStrings = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        assertThat(authorityStrings).containsExactly("SCOPE_read");
    }

    @Test
    void testJwtConverter_withoutAuthorizationClaim() {
        // GIVEN
        Jwt jwt = createJwt("read", Map.of("roles", List.of("user")), null);
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();

        // WHEN
        Collection<GrantedAuthority> authorities = converter.convert(jwt).getAuthorities();

        // THEN
        Set<String> authorityStrings = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        assertThat(authorityStrings).containsExactlyInAnyOrder("SCOPE_read", "ROLE_user");
    }

    /**
     * Méthode utilitaire qui construit une VRAIE instance de Jwt.
     * C'est la solution la plus robuste pour éviter les problèmes de mock.
     *
     * @param scope             La claim "scope", ex: "read write"
     * @param realmAccessClaim  La claim "realm_access", ex: Map.of("roles", List.of("user"))
     * @param authorizationClaim La claim "authorization", ex: Map.of("permissions", ...)
     * @return Une instance de Jwt
     */
    private Jwt createJwt(String scope, Map<String, Object> realmAccessClaim, Map<String, Object> authorizationClaim) {
        Instant now = Instant.now();
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("test-subject")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .claim("scope", scope); // La claim scope est essentielle

        if (realmAccessClaim != null) {
            builder.claim("realm_access", realmAccessClaim);
        }

        if (authorizationClaim != null) {
            builder.claim("authorization", authorizationClaim);
        }

        return builder.build();
    }
}