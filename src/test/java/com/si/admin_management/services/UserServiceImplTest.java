package com.si.admin_management.services;

import com.si.admin_management.dtos.keycloak.KcUserDto;
import com.si.admin_management.dtos.keycloak.KcUserDtoRequest;
import com.si.admin_management.services.users.UserServiceImpl;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl keycloakUserService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private Keycloak keycloakAdmin;


    @Value("${keycloak.realm}")
    private String realm;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private Response response;

    @Mock
    private UserResource userResource;

    @Mock
    private RolesResource rolesResource;

    @Mock
    private RoleResource roleResource;

    @Mock
    private RoleScopeResource roleScopeResource;


    private KcUserDtoRequest kcUserDtoRequest;
    private UserRepresentation userRepresentation;
    private RoleRepresentation roleRepresentation;


    @BeforeEach
    public void setup() {
        // Configuration du realm
        when(keycloakAdmin.realm(eq(realm))).thenReturn(realmResource);

        // Configuration pour la création des utilisateurs
        when(realmResource.users()).thenReturn(usersResource);

        // Initialisation de KcUserDtoRequest
        kcUserDtoRequest = new KcUserDtoRequest();
        kcUserDtoRequest.setFirstName("John");
        kcUserDtoRequest.setLastName("Doe");
        kcUserDtoRequest.setEmail("john.doe@example.com");
        kcUserDtoRequest.setPassword("Password123!");
        kcUserDtoRequest.setRole("user_role");

        // Préparation de UserRepresentation pour le mock de la réponse de recherche
        userRepresentation = new UserRepresentation();
        userRepresentation.setId("user-123");
        userRepresentation.setFirstName("John");
        userRepresentation.setLastName("Doe");
        userRepresentation.setEmail("john.doe@example.com");

        // Préparation du rôle
        roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("user_role");
    }

//    @Test
//    public void testCreateUser_Success() {
//        // Mock pour la création d'utilisateur
//        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
//        when(response.getStatus()).thenReturn(201);
//
//        // Mock pour la recherche d'utilisateur après création
//        when(usersResource.search(eq(kcUserDtoRequest.getEmail()))).thenReturn(List.of(userRepresentation));
//
//        // Mock pour obtenir la ressource utilisateur
//        when(usersResource.get(eq("user-123"))).thenReturn(userResource);
//
//        // Mock pour la gestion des rôles
//        when(realmResource.roles()).thenReturn(rolesResource);
//        when(rolesResource.get(eq(kcUserDtoRequest.getRole()))).thenReturn(roleResource);
//        when(roleResource.toRepresentation()).thenReturn(roleRepresentation);
//        //when(userResource.roles()).thenReturn(roleScopeResource);
//        //when(roleScopeResource.realmLevel()).thenReturn(roleScopeResource);
//
//        // Exécution de la méthode à tester
//        KcUserDto result = keycloakUserService.createUser(kcUserDtoRequest);
//
//        // Vérifications
//        assertNotNull(result);
//        assertEquals("user-123", result.getId());
//        assertEquals("John", result.getFirstName());
//        assertEquals("Doe", result.getLastName());
//        assertEquals("john.doe@example.com", result.getEmail());
//
//        // Vérification que la création d'utilisateur a été appelée avec les bonnes valeurs
//        ArgumentCaptor<UserRepresentation> userCaptor = ArgumentCaptor.forClass(UserRepresentation.class);
//        verify(usersResource).create(userCaptor.capture());
//        UserRepresentation capturedUser = userCaptor.getValue();
//
//        assertEquals(kcUserDtoRequest.getFirstName(), capturedUser.getFirstName());
//        assertEquals(kcUserDtoRequest.getLastName(), capturedUser.getLastName());
//        assertEquals(kcUserDtoRequest.getEmail(), capturedUser.getEmail());
//        assertEquals(kcUserDtoRequest.getEmail(), capturedUser.getUsername());
//        assertTrue(capturedUser.isEmailVerified());
//        assertTrue(capturedUser.isEnabled());
//        assertEquals(Collections.emptyList(), capturedUser.getRequiredActions());
//
//        // Vérification que le mot de passe a été défini
//        verify(userResource).resetPassword(any());
//
//        // Vérification que le rôle a été attribué
//        verify(roleScopeResource).add(any());
//    }

    @Test
    public void testCreateUser_Failure() {
        // Mock pour simuler un échec lors de la création
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(400); // Code d'erreur

        // Exécution de la méthode à tester
        KcUserDto result = keycloakUserService.createUser(kcUserDtoRequest);

        // Vérifications
        assertNull(result);

        // Vérifier que les méthodes suivantes n'ont pas été appelées en cas d'échec
        verify(usersResource, never()).search(anyString());
        verify(userResource, never()).resetPassword(any());
        verify(roleScopeResource, never()).add(any());
    }

}
