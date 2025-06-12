package com.si.admin_management.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class KcAuthzInitConfigTest {

    @Mock
    private AuthzClient mockedAuthzClient;

    // Un captor pour vérifier l'objet Configuration
    @Captor
    private ArgumentCaptor<Configuration> configurationCaptor;

    @Test
    void authzClient_shouldCreateClientWithCorrectConfiguration() {
        // --- Given (Arrange) ---

        // 1. Instancier la classe de configuration à tester
        KcAuthzInitConfig config = new KcAuthzInitConfig();

        // 2. Simuler l'injection @Value en utilisant la réflexion
        //    C'est la partie cruciale pour donner des valeurs aux champs privés.
        ReflectionTestUtils.setField(config, "serverUrl", "http://localhost:8080/auth");
        ReflectionTestUtils.setField(config, "realm", "test-realm");
        ReflectionTestUtils.setField(config, "clientId", "test-client");
        ReflectionTestUtils.setField(config, "clientSecret", "my-super-secret");

        // 3. Intercepter l'appel à la méthode statique AuthzClient.create()
        //    On utilise un bloc try-with-resources pour s'assurer que le mock statique est
        //    bien "fermé" après le test et ne pollue pas les autres tests.
        try (MockedStatic<AuthzClient> mockedStaticAuthzClient = mockStatic(AuthzClient.class)) {

            // On programme le comportement du mock statique :
            // "Quand AuthzClient.create est appelé avec n'importe quel objet Configuration,
            // retourne notre client mocké."
            mockedStaticAuthzClient.when(() -> AuthzClient.create(any(Configuration.class)))
                    .thenReturn(mockedAuthzClient);

            // --- When (Act) ---
            AuthzClient resultClient = config.authzClient();

            // --- Then (Assert) ---

            // A. Vérifier que la méthode statique a bien été appelée avec un objet Configuration
            mockedStaticAuthzClient.verify(() -> AuthzClient.create(configurationCaptor.capture()));

            // B. Capturer et vérifier le contenu de l'objet Configuration
            Configuration capturedConfig = configurationCaptor.getValue();
            assertThat(capturedConfig.getAuthServerUrl()).isEqualTo("http://localhost:8080/auth");
            assertThat(capturedConfig.getRealm()).isEqualTo("test-realm");
            assertThat(capturedConfig.getResource()).isEqualTo("test-client");

            Map<String, Object> credentials = capturedConfig.getCredentials();
            assertThat(credentials).isNotNull();
            assertThat(credentials.get("secret")).isEqualTo("my-super-secret");

            // C. Vérifier que le bean retourné est bien celui que notre mock a fourni
            assertThat(resultClient).isSameAs(mockedAuthzClient);
        }
    }
}
