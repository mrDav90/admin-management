package com.si.admin_management.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KeycloakAdminConfigTest {
    // Mock pour le Builder
    @Mock
    private KeycloakBuilder mockedBuilder;

    // Mock pour l'objet Keycloak final
    @Mock
    private Keycloak mockedKeycloakInstance;

    @Test
    void keycloakAdmin_shouldBuildClientWithCorrectParameters() {
        // --- Given (Arrange) ---

        // 1. Instancier la classe de configuration
        KeycloakAdminConfig config = new KeycloakAdminConfig();

        // 2. Simuler l'injection des @Value
        String serverUrl = "http://keycloak.test:8080";
        String realm = "my-app-realm";
        String clientId = "my-admin-client";
        String clientSecret = "a-very-secret-secret";

        ReflectionTestUtils.setField(config, "serverUrl", serverUrl);
        ReflectionTestUtils.setField(config, "realm", realm);
        ReflectionTestUtils.setField(config, "clientId", clientId);
        ReflectionTestUtils.setField(config, "clientSecret", clientSecret);

        // 3. Intercepter l'appel statique KeycloakBuilder.builder()
        try (MockedStatic<KeycloakBuilder> mockedStaticBuilder = mockStatic(KeycloakBuilder.class)) {

            // Quand KeycloakBuilder.builder() est appelé, on retourne notre propre builder mocké.
            mockedStaticBuilder.when(KeycloakBuilder::builder).thenReturn(mockedBuilder);

            // 4. Configurer la chaîne d'appels du builder mocké.
            // Chaque appel doit retourner le mock lui-même pour permettre l'enchaînement.
            when(mockedBuilder.serverUrl(anyString())).thenReturn(mockedBuilder);
            when(mockedBuilder.realm(anyString())).thenReturn(mockedBuilder);
            when(mockedBuilder.clientId(anyString())).thenReturn(mockedBuilder);
            when(mockedBuilder.clientSecret(anyString())).thenReturn(mockedBuilder);
            when(mockedBuilder.grantType(anyString())).thenReturn(mockedBuilder);

            // L'appel final .build() doit retourner notre instance finale mockée de Keycloak.
            when(mockedBuilder.build()).thenReturn(mockedKeycloakInstance);

            // --- When (Act) ---
            Keycloak resultKeycloak = config.keycloakAdmin();

            // --- Then (Assert) ---

            // A. Vérifier que chaque méthode du builder a été appelée avec la bonne valeur.
            verify(mockedBuilder).serverUrl(serverUrl);
            verify(mockedBuilder).realm(realm);
            verify(mockedBuilder).clientId(clientId);
            verify(mockedBuilder).clientSecret(clientSecret);
            verify(mockedBuilder).grantType(OAuth2Constants.CLIENT_CREDENTIALS);

            // B. Vérifier que la méthode finale .build() a bien été appelée.
            verify(mockedBuilder).build();

            // C. Vérifier que le bean retourné est bien l'instance que notre builder a "construite".
            assertThat(resultKeycloak).isSameAs(mockedKeycloakInstance);
        }
    }
}
