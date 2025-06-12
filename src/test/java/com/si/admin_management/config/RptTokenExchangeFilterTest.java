package com.si.admin_management.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.resource.AuthorizationResource;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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

    @Captor
    private ArgumentCaptor<HttpServletRequest> requestCaptor;

    @InjectMocks
    private RptTokenExchangeFilter rptTokenExchangeFilter;


    @BeforeEach
    void setUp(){
        rptTokenExchangeFilter = new RptTokenExchangeFilter(authzConfig);

        assertNotNull(rptTokenExchangeFilter, "Le filtre ne doit pas être null");
        assertNotNull(request, "Le mock 'request' ne doit pas être null");
        assertNotNull(response, "Le mock 'response' ne doit pas être null");
        assertNotNull(filterChain, "Le mock 'filterChain' ne doit pas être null");
    }


    @Test
    void doFilterInternal_whenNoAuthorizationHeader_thenProceedsWithOriginalRequest() throws ServletException, IOException {

        when(request.getHeader("Authorization")).thenReturn(null);

        rptTokenExchangeFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(authzConfig);
    }

    @Test
    void doFilterInternal_whenAuthorizationHeaderIsNotBearer_thenProceedsWithOriginalRequest() throws ServletException, IOException {

        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNzd29yZA==");
        rptTokenExchangeFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(authzConfig);
    }

    @Test
    void doFilterInternal_whenTokenExchangeSucceeds_thenProceedsWithWrappedRequestContainingRpt() throws ServletException, IOException {
        String originalAccessToken = "original-access-token-123";
        String rptToken = "new-rpt-token-789";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + originalAccessToken);

        when(authzConfig.authzClient()).thenReturn(authzClient);
        when(authzClient.authorization(originalAccessToken)).thenReturn(authorizationResource);
        when(authorizationResource.authorize()).thenReturn(authorizationResponse);
        when(authorizationResponse.getToken()).thenReturn(rptToken);

        rptTokenExchangeFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(requestCaptor.capture(), eq(response));

        HttpServletRequest wrappedRequest = requestCaptor.getValue();
        assertThat(wrappedRequest).isInstanceOf(HttpServletRequestWrapper.class);
        assertThat(wrappedRequest.getHeader("Authorization")).isEqualTo("Bearer " + rptToken);

        when(request.getHeader("X-Custom-Header")).thenReturn("some-value");
        assertThat(wrappedRequest.getHeader("X-Custom-Header")).isEqualTo("some-value");

        verify(response, never()).setStatus(anyInt());
        verify(response, never()).getWriter();
    }

    @Test
    void doFilterInternal_whenTokenExchangeFails_thenReturnsForbiddenAndStopsChain() throws ServletException, IOException {
        String originalAccessToken = "original-access-token-123";
        String errorMessage = "Connection to Keycloak failed";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + originalAccessToken);

        when(authzConfig.authzClient()).thenReturn(authzClient);
        when(authzClient.authorization(originalAccessToken)).thenReturn(authorizationResource);
        when(authorizationResource.authorize()).thenThrow(new RuntimeException(errorMessage));

        when(response.getWriter()).thenReturn(printWriter);

        rptTokenExchangeFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.FORBIDDEN.value());

        verify(printWriter).write("Unable to get RPT: " + errorMessage);

        verifyNoInteractions(filterChain);
    }
}