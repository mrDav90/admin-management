package com.si.admin_management.services.users;

import com.si.admin_management.dtos.keycloak.KcUserDto;
import com.si.admin_management.dtos.keycloak.KcUserDtoRequest;
import com.si.admin_management.dtos.keycloak.Permission;
import com.si.admin_management.dtos.keycloak.UserInfos;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private Keycloak keycloakAdmin;

    @Mock
    private MessageSource messageSource;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private RolesResource rolesResource;

    @Mock
    private RoleResource roleResource;

    @Mock
    private Response response;

    @Mock
    private RoleMappingResource roleMappingResource;

    @Mock
    private RoleScopeResource roleScopeResource;

    @Mock
    private Logger logger;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @Value("${keycloak.realm}")
    private String realm;

    @InjectMocks
    private UserServiceImpl userService;


    @BeforeEach
    void setUp() {
    }

    @Test
    void createUser_ShouldReturnKcUserDto_WhenUserCreatedSuccessfully() {
        // Given
        KcUserDtoRequest request = new KcUserDtoRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        request.setRole("USER");

        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setId("user-id-123");
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setEmail("john.doe@example.com");

        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("USER");

        // Mock chain setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        when(usersResource.search("john.doe@example.com")).thenReturn(List.of(existingUser));
        when(usersResource.get("user-id-123")).thenReturn(userResource);
        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get("USER")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(roleRepresentation);
        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);

        // When
        KcUserDto result = userService.createUser(request);

        // Then
        assertNotNull(result);
        assertEquals("user-id-123", result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());

        // Verify interactions
        verify(usersResource).create(argThat(user ->
                "John".equals(user.getFirstName()) &&
                        "Doe".equals(user.getLastName()) &&
                        "john.doe@example.com".equals(user.getUsername()) &&
                        "john.doe@example.com".equals(user.getEmail()) &&
                        user.isEmailVerified() &&
                        user.isEnabled() &&
                        user.getRequiredActions().isEmpty()
        ));

        verify(userResource).resetPassword(argThat(cred ->
                !cred.isTemporary() &&
                        CredentialRepresentation.PASSWORD.equals(cred.getType()) &&
                        "password123".equals(cred.getValue())
        ));

        verify(roleScopeResource).add(List.of(roleRepresentation));
        //verify(logger).info("User created : {}", result);
    }

    @Test
    void createUser_ShouldReturnNull_WhenUserCreationFails() {
        // Given
        KcUserDtoRequest request = new KcUserDtoRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        request.setRole("USER");

        // Mock chain setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(400); // Bad Request

        // When
        KcUserDto result = userService.createUser(request);

        // Then
        assertNull(result);

        // Verify that no further operations were attempted
        verify(usersResource, never()).search(anyString());
        //verify(logger, never()).info(anyString(), any());
    }


    @Test
    void getUsers_ShouldReturnPageWithUsers_WhenUsersExist() {
        // Given
        int pageNumber = 0;
        int pageSize = 10;
        int firstResult = 0; // pageNumber * pageSize

        UserRepresentation user1 = createUserRepresentation("user1", "john.doe", "John", "Doe", "john@example.com", true);
        UserRepresentation user2 = createUserRepresentation("user2", "jane.smith", "Jane", "Smith", "jane@example.com", true);
        UserRepresentation user3 = createUserRepresentation("user3", "bob.wilson", "Bob", "Wilson", "bob@example.com", false);

        List<UserRepresentation> mockUsers = Arrays.asList(user1, user2, user3);
        int totalUsers = 25;

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.list(firstResult, pageSize)).thenReturn(mockUsers);
        when(usersResource.count()).thenReturn(totalUsers);

        // When
        Page<KcUserDto> result = userService.getUsers(pageNumber, pageSize);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(totalUsers, result.getTotalElements());
        assertEquals(pageNumber, result.getNumber());
        assertEquals(pageSize, result.getSize());
        assertEquals(3, result.getTotalPages()); // ceil(25/10) = 3

        // Verify first user mapping
        KcUserDto firstUser = result.getContent().get(0);
        assertEquals("user1", firstUser.getId());
        assertEquals("john.doe", firstUser.getUsername());
        assertEquals("John", firstUser.getFirstName());
        assertEquals("Doe", firstUser.getLastName());
        assertEquals("john@example.com", firstUser.getEmail());
        assertTrue(firstUser.isEnabled());

        // Verify second user mapping
        KcUserDto secondUser = result.getContent().get(1);
        assertEquals("user2", secondUser.getId());
        assertEquals("jane.smith", secondUser.getUsername());
        assertEquals("Jane", secondUser.getFirstName());
        assertEquals("Smith", secondUser.getLastName());
        assertEquals("jane@example.com", secondUser.getEmail());
        assertTrue(secondUser.isEnabled());

        // Verify third user mapping (disabled user)
        KcUserDto thirdUser = result.getContent().get(2);
        assertEquals("user3", thirdUser.getId());
        assertFalse(thirdUser.isEnabled());

        // Verify method calls
        verify(usersResource).list(firstResult, pageSize);
        verify(usersResource).count();
    }

    @Test
    void getUsers_ShouldReturnEmptyPage_WhenNoUsersExist() {
        // Given
        int pageNumber = 0;
        int pageSize = 10;
        int firstResult = 0;

        List<UserRepresentation> emptyUsers = Collections.emptyList();
        int totalUsers = 0;

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.list(firstResult, pageSize)).thenReturn(emptyUsers);
        when(usersResource.count()).thenReturn(totalUsers);

        // When
        Page<KcUserDto> result = userService.getUsers(pageNumber, pageSize);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(pageNumber, result.getNumber());
        assertEquals(pageSize, result.getSize());
        assertEquals(0, result.getTotalPages());

        verify(usersResource).list(firstResult, pageSize);
        verify(usersResource).count();
    }

    @Test
    void getUsers_ShouldCalculateFirstResultCorrectly_ForDifferentPages() {
        // Given
        int pageNumber = 2;
        int pageSize = 5;
        int expectedFirstResult = 10; // pageNumber * pageSize = 2 * 5 = 10

        List<UserRepresentation> mockUsers = Arrays.asList(
                createUserRepresentation("user1", "user1", "User", "One", "user1@example.com", true)
        );
        int totalUsers = 15;

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.list(expectedFirstResult, pageSize)).thenReturn(mockUsers);
        when(usersResource.count()).thenReturn(totalUsers);

        // When
        Page<KcUserDto> result = userService.getUsers(pageNumber, pageSize);

        // Then
        assertNotNull(result);
        assertEquals(pageNumber, result.getNumber());
        assertEquals(pageSize, result.getSize());
        assertEquals(totalUsers, result.getTotalElements());

        // Verify that the correct firstResult was calculated and used
        verify(usersResource).list(expectedFirstResult, pageSize);
    }

    @Test
    void getUsers_ShouldHandleNullValues_InUserRepresentation() {
        // Given
        int pageNumber = 0;
        int pageSize = 10;

        UserRepresentation userWithNulls = new UserRepresentation();
        userWithNulls.setId("user-with-nulls");
        userWithNulls.setUsername(null);
        userWithNulls.setFirstName(null);
        userWithNulls.setLastName(null);
        userWithNulls.setEmail(null);
        userWithNulls.setEnabled(false);

        List<UserRepresentation> mockUsers = Arrays.asList(userWithNulls);
        int totalUsers = 1;

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.list(0, pageSize)).thenReturn(mockUsers);
        when(usersResource.count()).thenReturn(totalUsers);

        // When
        Page<KcUserDto> result = userService.getUsers(pageNumber, pageSize);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        KcUserDto userDto = result.getContent().get(0);
        assertEquals("user-with-nulls", userDto.getId());
        assertNull(userDto.getUsername());
        assertNull(userDto.getFirstName());
        assertNull(userDto.getLastName());
        assertNull(userDto.getEmail());
        // enabled should handle null Boolean correctly based on your KcUserDto implementation
    }

    @Test
    void getUsers_ShouldCreateCorrectPageable() {
        // Given
        int pageNumber = 1;
        int pageSize = 20;

        List<UserRepresentation> mockUsers = Collections.emptyList();
        int totalUsers = 0;

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.list(anyInt(), anyInt())).thenReturn(mockUsers);
        when(usersResource.count()).thenReturn(totalUsers);

        // When
        Page<KcUserDto> result = userService.getUsers(pageNumber, pageSize);

        // Then
        assertNotNull(result);
        Pageable pageable = result.getPageable();
        assertEquals(pageNumber, pageable.getPageNumber());
        assertEquals(pageSize, pageable.getPageSize());
    }

    @Test
    void getUsers_ShouldHandleLargePageNumbers() {
        // Given
        int pageNumber = 10;
        int pageSize = 50;
        int expectedFirstResult = 500; // 10 * 50

        List<UserRepresentation> mockUsers = Collections.emptyList();
        int totalUsers = 1000;

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.list(expectedFirstResult, pageSize)).thenReturn(mockUsers);
        when(usersResource.count()).thenReturn(totalUsers);

        // When
        Page<KcUserDto> result = userService.getUsers(pageNumber, pageSize);

        // Then
        assertNotNull(result);
        assertEquals(pageNumber, result.getNumber());
        assertEquals(totalUsers, result.getTotalElements());
        assertEquals(20, result.getTotalPages()); // ceil(1000/50) = 20

        verify(usersResource).list(expectedFirstResult, pageSize);
    }

    // Helper method to create UserRepresentation for testing
    private UserRepresentation createUserRepresentation(String id, String username, String firstName,
                                                        String lastName, String email, Boolean enabled) {
        UserRepresentation user = new UserRepresentation();
        user.setId(id);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setEnabled(enabled);
        return user;
    }


    @Test
    void updateUser_ShouldUpdateUserSuccessfully_WhenValidDataProvided() {
        // Given
        String userId = "user-123";
        KcUserDtoRequest request = new KcUserDtoRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");

        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setId(userId);
        existingUser.setFirstName("OldFirstName");
        existingUser.setLastName("OldLastName");
        existingUser.setEmail("old.email@example.com");
        existingUser.setUsername("old.username");

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(existingUser);

        // When
        userService.updateUser(userId, request);

        // Then
        verify(userResource).toRepresentation();
        verify(userResource).update(argThat(user ->
                "John".equals(user.getFirstName()) &&
                        "Doe".equals(user.getLastName()) &&
                        "john.doe@example.com".equals(user.getEmail()) &&
                        "john.doe@example.com".equals(user.getUsername()) &&
                        userId.equals(user.getId()) // Vérifie que l'ID n'a pas été modifié
        ));
    }

    @Test
    void updateUser_ShouldPreserveOtherUserProperties_WhenUpdating() {
        // Given
        String userId = "user-456";
        KcUserDtoRequest request = new KcUserDtoRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@example.com");

        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setId(userId);
        existingUser.setFirstName("OldJane");
        existingUser.setLastName("OldSmith");
        existingUser.setEmail("old.jane@example.com");
        existingUser.setUsername("old.jane");
        existingUser.setEnabled(true);
        existingUser.setEmailVerified(true);

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(existingUser);

        // When
        userService.updateUser(userId, request);

        // Then
        verify(userResource).update(argThat(user -> {
            // Vérifie que les nouvelles valeurs sont bien définies
            assertEquals("Jane", user.getFirstName());
            assertEquals("Smith", user.getLastName());
            assertEquals("jane.smith@example.com", user.getEmail());
            assertEquals("jane.smith@example.com", user.getUsername());

            // Vérifie que les autres propriétés sont préservées
            assertEquals(userId, user.getId());
            assertEquals(true, user.isEnabled());
            assertEquals(true, user.isEmailVerified());

            return true;
        }));
    }

    @Test
    void updateUser_ShouldHandleNullValues_InRequest() {
        // Given
        String userId = "user-789";
        KcUserDtoRequest request = new KcUserDtoRequest();
        request.setFirstName(null);
        request.setLastName(null);
        request.setEmail(null);

        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setId(userId);
        existingUser.setFirstName("ExistingFirst");
        existingUser.setLastName("ExistingLast");
        existingUser.setEmail("existing@example.com");
        existingUser.setUsername("existing.user");

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(existingUser);

        // When
        userService.updateUser(userId, request);

        // Then
        verify(userResource).update(argThat(user -> {
            // Vérifie que les valeurs nulles sont bien définies
            assertNull(user.getFirstName());
            assertNull(user.getLastName());
            assertNull(user.getEmail());
            assertNull(user.getUsername()); // username = email
            assertEquals(userId, user.getId());
            return true;
        }));
    }

    @Test
    void updateUser_ShouldSetUsernameToEmail_Always() {
        // Given
        String userId = "user-username-test";
        KcUserDtoRequest request = new KcUserDtoRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("test.user@example.com");

        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setId(userId);
        existingUser.setUsername("different.username");
        existingUser.setEmail("different@email.com");

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(existingUser);

        // When
        userService.updateUser(userId, request);

        // Then
        verify(userResource).update(argThat(user -> {
            // Vérifie que username = email toujours
            assertEquals("test.user@example.com", user.getEmail());
            assertEquals("test.user@example.com", user.getUsername());
            return true;
        }));
    }

    @Test
    void updateUser_ShouldCallKeycloakMethodsInCorrectOrder() {
        // Given
        String userId = "user-order-test";
        KcUserDtoRequest request = new KcUserDtoRequest();
        request.setFirstName("Order");
        request.setLastName("Test");
        request.setEmail("order.test@example.com");

        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setId(userId);

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(existingUser);

        // When
        userService.updateUser(userId, request);

        // Then - Vérifie l'ordre des appels
        var inOrder = inOrder(keycloakAdmin, realmResource, usersResource, userResource);
        inOrder.verify(keycloakAdmin).realm(realm);
        inOrder.verify(realmResource).users();
        inOrder.verify(usersResource).get(userId);
        inOrder.verify(userResource).toRepresentation();
        inOrder.verify(keycloakAdmin).realm(realm); // Deuxième appel pour l'update
        inOrder.verify(realmResource).users();
        inOrder.verify(usersResource).get(userId);
        inOrder.verify(userResource).update(any(UserRepresentation.class));
    }

    @Test
    void updateUser_ShouldHandleEmptyStrings_InRequest() {
        // Given
        String userId = "user-empty-strings";
        KcUserDtoRequest request = new KcUserDtoRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("");

        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setId(userId);
        existingUser.setFirstName("NonEmpty");
        existingUser.setLastName("NonEmpty");
        existingUser.setEmail("nonempty@example.com");

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(existingUser);

        // When
        userService.updateUser(userId, request);

        // Then
        verify(userResource).update(argThat(user -> {
            assertEquals("", user.getFirstName());
            assertEquals("", user.getLastName());
            assertEquals("", user.getEmail());
            assertEquals("", user.getUsername());
            return true;
        }));
    }

    @Test
    void updateUser_ShouldNotModifyUserId_DuringUpdate() {
        // Given
        String userId = "immutable-user-id";
        KcUserDtoRequest request = new KcUserDtoRequest();
        request.setFirstName("New");
        request.setLastName("Name");
        request.setEmail("new.email@example.com");

        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setId(userId);
        existingUser.setFirstName("Old");
        existingUser.setLastName("Name");

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(existingUser);

        // When
        userService.updateUser(userId, request);

        // Then
        verify(userResource).update(argThat(user -> {
            // L'ID ne doit jamais changer
            assertEquals(userId, user.getId());
            return true;
        }));
    }

//    private void assertEquals(Object expected, Object actual) {
//        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
//    }
//
//    private void assertNull(Object actual) {
//        org.junit.jupiter.api.Assertions.assertNull(actual);
//    }


    @Test
    void deleteUser_ShouldDeleteUserSuccessfully_WhenUserExists() {
        // Given
        String username = "john.doe";
        String userId = "user-123";

        UserRepresentation user = new UserRepresentation();
        user.setId(userId);
        user.setUsername(username);

        List<UserRepresentation> users = Arrays.asList(user);

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.search(username)).thenReturn(users);
        when(usersResource.get(userId)).thenReturn(userResource);

        // When
        userService.deleteUser(username);

        // Then
        verify(usersResource).search(username);
        verify(usersResource).get(userId);
        verify(userResource).remove();
    }

    @Test
    void deleteUser_ShouldPrintMessage_WhenUserNotFound() {
        // Given
        String username = "nonexistent.user";
        List<UserRepresentation> emptyUsers = Collections.emptyList();

        // Capture System.out
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Mock setup
            when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
            when(realmResource.users()).thenReturn(usersResource);
            when(usersResource.search(username)).thenReturn(emptyUsers);

            // When
            userService.deleteUser(username);

            // Then
            verify(usersResource).search(username);
            verify(usersResource, never()).get(anyString());
            verify(userResource, never()).remove();

            String output = outputStream.toString();
            assertTrue(output.contains("Utilisateur non trouvé !"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void deleteUser_ShouldDeleteFirstUser_WhenMultipleUsersFound() {
        // Given
        String username = "common.name";
        String firstUserId = "user-first";
        String secondUserId = "user-second";

        UserRepresentation firstUser = new UserRepresentation();
        firstUser.setId(firstUserId);
        firstUser.setUsername(username);

        UserRepresentation secondUser = new UserRepresentation();
        secondUser.setId(secondUserId);
        secondUser.setUsername(username);

        List<UserRepresentation> users = Arrays.asList(firstUser, secondUser);

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.search(username)).thenReturn(users);
        when(usersResource.get(firstUserId)).thenReturn(userResource);

        // When
        userService.deleteUser(username);

        // Then
        verify(usersResource).search(username);
        verify(usersResource).get(firstUserId); // Premier utilisateur seulement
        verify(usersResource, never()).get(secondUserId);
        verify(userResource).remove();
    }

    @Test
    void deleteUser_ShouldHandleNullUsername() {
        // Given
        String username = null;

        // Mock setup
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.search(username)).thenReturn(Collections.emptyList());

        // When & Then - Ne devrait pas lever d'exception
        assertDoesNotThrow(() -> userService.deleteUser(username));

        verify(usersResource).search(username);
    }

    @Test
    void getMe_ShouldReturnUserInfos_WhenJwtIsValid() {
        // Given
        Map<String, Object> authorizationClaim = new HashMap<>();
        List<Permission> permissions = Arrays.asList(
                createPermission("READ", "USER"),
                createPermission("WRITE", "ADMIN")
        );
        authorizationClaim.put("permissions", permissions);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            // Mock setup
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);

            when(jwt.getClaimAsString("preferred_username")).thenReturn("john.doe");
            when(jwt.getClaimAsString("email")).thenReturn("john.doe@example.com");
            when(jwt.getClaimAsString("given_name")).thenReturn("John");
            when(jwt.getClaimAsString("family_name")).thenReturn("Doe");
            when(jwt.getClaimAsMap("authorization")).thenReturn(authorizationClaim);

            // When
            UserInfos result = userService.getMe();

            // Then
            assertNotNull(result);
            assertEquals("john.doe", result.getUsername());
            assertEquals("john.doe@example.com", result.getEmail());
            assertEquals("John", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            assertNotNull(result.getPermissions());
            assertEquals(2, result.getPermissions().size());
        }
    }

    @Test
    void getMe_ShouldHandleNullClaims() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            // Mock setup
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);

            when(jwt.getClaimAsString("preferred_username")).thenReturn(null);
            when(jwt.getClaimAsString("email")).thenReturn(null);
            when(jwt.getClaimAsString("given_name")).thenReturn(null);
            when(jwt.getClaimAsString("family_name")).thenReturn(null);
            when(jwt.getClaimAsMap("authorization")).thenReturn(null);

            // When
            UserInfos result = userService.getMe();

            // Then
            assertNotNull(result);
            assertNull(result.getUsername());
            assertNull(result.getEmail());
            assertNull(result.getFirstName());
            assertNull(result.getLastName());
            assertNotNull(result.getPermissions());
            assertTrue(result.getPermissions().isEmpty());
        }
    }

    @Test
    void getMe_ShouldReturnEmptyPermissions_WhenAuthorizationClaimIsEmpty() {
        // Given
        Map<String, Object> emptyAuthorizationClaim = new HashMap<>();
        // Pas de clé "permissions"

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            // Mock setup
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);

            when(jwt.getClaimAsString("preferred_username")).thenReturn("testuser");
            when(jwt.getClaimAsString("email")).thenReturn("test@example.com");
            when(jwt.getClaimAsString("given_name")).thenReturn("Test");
            when(jwt.getClaimAsString("family_name")).thenReturn("User");
            when(jwt.getClaimAsMap("authorization")).thenReturn(emptyAuthorizationClaim);

            // When
            UserInfos result = userService.getMe();

            // Then
            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            assertNotNull(result.getPermissions());
            assertTrue(result.getPermissions().isEmpty());
        }
    }

    @Test
    void getMe_ShouldHandleEmptyPermissionsList() {
        // Given
        Map<String, Object> authorizationClaim = new HashMap<>();
        authorizationClaim.put("permissions", Collections.emptyList());

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            // Mock setup
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);

            when(jwt.getClaimAsString("preferred_username")).thenReturn("emptyuser");
            when(jwt.getClaimAsMap("authorization")).thenReturn(authorizationClaim);

            // When
            UserInfos result = userService.getMe();

            // Then
            assertNotNull(result);
            assertEquals("emptyuser", result.getUsername());
            assertNotNull(result.getPermissions());
            assertTrue(result.getPermissions().isEmpty());
        }
    }

    @Test
    void getMe_ShouldHandleNullPermissionsInAuthorization() {
        // Given
        Map<String, Object> authorizationClaim = new HashMap<>();
        authorizationClaim.put("permissions", null);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            // Mock setup
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);

            when(jwt.getClaimAsString("preferred_username")).thenReturn("nullpermuser");
            when(jwt.getClaimAsMap("authorization")).thenReturn(authorizationClaim);

            // When
            UserInfos result = userService.getMe();

            // Then
            assertNotNull(result);
            assertEquals("nullpermuser", result.getUsername());
            assertNotNull(result.getPermissions());
            assertTrue(result.getPermissions().isEmpty());
        }
    }

    @Test
    void getMe_ShouldExtractAllClaimsCorrectly() {
        // Given
        Map<String, Object> authorizationClaim = new HashMap<>();
        List<Permission> permissions = Arrays.asList(createPermission("ADMIN", "ALL"));
        authorizationClaim.put("permissions", permissions);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     mockStatic(SecurityContextHolder.class)) {

            // Mock setup
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);

            when(jwt.getClaimAsString("preferred_username")).thenReturn("admin.user");
            when(jwt.getClaimAsString("email")).thenReturn("admin@company.com");
            when(jwt.getClaimAsString("given_name")).thenReturn("Admin");
            when(jwt.getClaimAsString("family_name")).thenReturn("User");
            when(jwt.getClaimAsMap("authorization")).thenReturn(authorizationClaim);

            // When
            UserInfos result = userService.getMe();

            // Then
            assertNotNull(result);
            assertEquals("admin.user", result.getUsername());
            assertEquals("admin@company.com", result.getEmail());
            assertEquals("Admin", result.getFirstName());
            assertEquals("User", result.getLastName());
            assertEquals(1, result.getPermissions().size());

            // Verify all JWT claim methods were called
            verify(jwt).getClaimAsString("preferred_username");
            verify(jwt).getClaimAsString("email");
            verify(jwt).getClaimAsString("given_name");
            verify(jwt).getClaimAsString("family_name");
            verify(jwt).getClaimAsMap("authorization");
        }
    }

    // Helper method pour créer des permissions de test
    private Permission createPermission(String action, String resource) {
        Permission permission = new Permission();
        // Adaptez selon votre implémentation de Permission
        // permission.setAction(action);
        // permission.setResource(resource);
        return permission;
    }
}
