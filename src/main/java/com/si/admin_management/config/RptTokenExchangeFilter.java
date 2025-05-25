package com.si.admin_management.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RptTokenExchangeFilter extends OncePerRequestFilter {
    private final KcAuthzInitConfig authzConfig;


    RptTokenExchangeFilter(KcAuthzInitConfig authzConfig) {
        this.authzConfig = authzConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String originalAccessToken = authHeader.substring(7);

            try {
                String rpt = getRptToken(originalAccessToken);
                HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getHeader(String name) {
                        if ("Authorization".equalsIgnoreCase(name)) {
                            return "Bearer " + rpt;
                        }
                        return super.getHeader(name);
                    }
                };

                filterChain.doFilter(wrappedRequest, response);
                return;
            } catch (Exception e) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Unable to get RPT: " + e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getRptToken(String accessToken) {
        AuthorizationResponse response = authzConfig.authzClient().authorization(accessToken).authorize();
        return response.getToken();
    }
}
