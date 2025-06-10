package com.si.admin_management.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.resource.AuthorizationResource;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RptTokenExchangeFilterTest {

    @Mock
    private KcAuthzInitConfig authzConfig;

    @Mock
    private AuthzClient authzClient;

    @Mock
    private AuthorizationResource authorizationResource;

    @Mock
    private AuthorizationResponse authorizationResponse;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    private RptTokenExchangeFilter rptTokenExchangeFilter;

    private static final String ORIGINAL_TOKEN = "original-access-token";
    private static final String RPT_TOKEN = "rpt-token";
    private static final String BEARER_HEADER = "Bearer " + ORIGINAL_TOKEN;

    @BeforeEach
    void setUp() throws IOException {
        when(response.getWriter()).thenReturn(printWriter);
    }



    @Test
    void doFilterInternal_ShouldReturnForbidden_WhenTokenExchangeFails()
            throws ServletException, IOException {
        // Given
        String errorMessage = "Token exchange failed";
        when(request.getHeader("Authorization")).thenReturn(BEARER_HEADER);
        when(authzConfig.authzClient()).thenReturn(authzClient);
        when(authzClient.authorization(ORIGINAL_TOKEN)).thenReturn(authorizationResource);
        when(authorizationResource.authorize()).thenThrow(new RuntimeException(errorMessage));

        // When
        rptTokenExchangeFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.FORBIDDEN.value());
        verify(printWriter).write("Unable to get RPT: " + errorMessage);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_ShouldReturnForbidden_WhenAuthzClientThrowsException()
            throws ServletException, IOException {
        // Given
        String errorMessage = "Keycloak connection failed";
        when(request.getHeader("Authorization")).thenReturn(BEARER_HEADER);
        when(authzConfig.authzClient()).thenThrow(new RuntimeException(errorMessage));

        // When
        rptTokenExchangeFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpStatus.FORBIDDEN.value());
        verify(printWriter).write("Unable to get RPT: " + errorMessage);
        verify(filterChain, never()).doFilter(any(), any());
    }

}