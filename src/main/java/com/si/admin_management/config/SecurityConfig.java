package com.si.admin_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http , RptTokenExchangeFilter rptTokenExchangeFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .addFilterBefore(rptTokenExchangeFilter, BearerTokenAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/swagger/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt( jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
            Collection<GrantedAuthority> authorities = defaultConverter.convert(jwt);

            List<GrantedAuthority> roles = extractRoles(jwt);
            List<GrantedAuthority> permissions = extractPermissions(jwt);

            return Stream.concat(
                    Stream.concat(
                            authorities.stream(),
                            roles.stream()
                    ),
                    permissions.stream()
            ).collect(Collectors.toList());
        });

        return converter;
    }

    private List<GrantedAuthority> extractRoles(Jwt jwt) {
        List<GrantedAuthority> rolesAuthorities = List.of();

        if (jwt.getClaim("realm_access") != null) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess.containsKey("roles")) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                rolesAuthorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
            }
        }

        return rolesAuthorities;
    }

    private List<GrantedAuthority> extractPermissions(Jwt jwt) {
        List<GrantedAuthority> permissionAuthorities = List.of();

        if (jwt.getClaim("authorization") != null) {
            Map<String, Object> authorization = jwt.getClaim("authorization");
            if (authorization.containsKey("permissions")) {
                List<Map<String, Object>> permissions = (List<Map<String, Object>>) authorization.get("permissions");

                permissionAuthorities = permissions.stream()
                        .flatMap(permission -> {
                            String resourceName = (String) permission.get("rsname");
                            List<String> scopes = (List<String>) permission.get("scopes");

                            return scopes.stream()
                                    .map(scope -> {
                                        // Format: PERMISSION_resource:scope
                                        return new SimpleGrantedAuthority("PERMISSION_" + resourceName + ":" + scope);
                                    });
                        })
                        .collect(Collectors.toList());
            }
        }

        return permissionAuthorities;
    }
}
