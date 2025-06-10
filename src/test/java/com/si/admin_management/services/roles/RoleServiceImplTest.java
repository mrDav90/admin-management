package com.si.admin_management.services.roles;

import com.si.admin_management.dtos.keycloak.KcRoleDto;
import com.si.admin_management.dtos.keycloak.KcRoleDtoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {
    @Mock
    private Keycloak keycloakAdmin;

    @Mock
    private RealmResource realmResource;

    @Mock
    private RolesResource rolesResource;

    @Mock
    private RoleResource roleResource;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;

    @BeforeEach
    void setUp() {
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.roles()).thenReturn(rolesResource);

    }

    @Test
    void createRole_shouldCreateRoleSuccessfully() {
        // Given
        KcRoleDtoRequest request = new KcRoleDtoRequest();
        request.setName("TEST_ROLE");
        request.setDescription("Test role description");

        RoleRepresentation createdRole = new RoleRepresentation();
        createdRole.setId("role-uuid-123");
        createdRole.setName("TEST_ROLE");
        createdRole.setDescription("Test role description");

        when(rolesResource.get("TEST_ROLE")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(createdRole);

        // Mock de la méthode createPolicy (en supposant qu'elle existe dans la classe)
        RoleServiceImpl spyService = spy(roleService);
        doNothing().when(spyService).createPolicy("TEST_ROLE");

        // When
        KcRoleDto result = spyService.createRole(request);

        // Then
        assertNotNull(result);
        assertEquals("role-uuid-123", result.getId());
        assertEquals("TEST_ROLE", result.getName());
        assertEquals("Test role description", result.getDescription());

        // Vérifications des interactions
        verify(keycloakAdmin).realm(realm);
        verify(realmResource).roles();
        verify(rolesResource).create(any(RoleRepresentation.class));
        verify(rolesResource).get("TEST_ROLE");
        verify(roleResource).toRepresentation();
        verify(spyService).createPolicy("TEST_ROLE");
    }


    @Test
    void getRoles_shouldReturnFirstPageWithCorrectPagination() {
        // Given
        int pageNumber = 0;
        int pageSize = 2;
        int firstResult = 0;

        List<RoleRepresentation> pagedRoles = createRoleRepresentations(
                Arrays.asList("ROLE_1", "ROLE_2"),
                Arrays.asList("Role 1 description", "Role 2 description")
        );

        List<RoleRepresentation> allRoles = createRoleRepresentations(
                Arrays.asList("ROLE_1", "ROLE_2", "ROLE_3", "ROLE_4", "ROLE_5"),
                Arrays.asList("Role 1 description", "Role 2 description", "Role 3 description",
                        "Role 4 description", "Role 5 description")
        );

        when(rolesResource.list(firstResult, pageSize)).thenReturn(pagedRoles);
        when(rolesResource.list()).thenReturn(allRoles);

        // When
        Page<KcRoleDto> result = roleService.getRoles(pageNumber, pageSize);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(2, result.getSize());
        assertFalse(result.isLast());
        assertTrue(result.isFirst());

        // Vérification du contenu
        KcRoleDto firstRole = result.getContent().get(0);
        assertEquals("role-1-id", firstRole.getId());
        assertEquals("ROLE_1", firstRole.getName());
        assertEquals("Role 1 description", firstRole.getDescription());

        // Vérifications des interactions
        verify(rolesResource).list(firstResult, pageSize);
        verify(rolesResource).list();
    }


    private List<RoleRepresentation> createRoleRepresentations(List<String> names, List<String> descriptions) {
        List<RoleRepresentation> roles = new ArrayList<>();

        for (int i = 0; i < names.size(); i++) {
            RoleRepresentation role = new RoleRepresentation();
            role.setId("role-" + (i + 1) + "-id");
            role.setName(names.get(i));
            role.setDescription(i < descriptions.size() ? descriptions.get(i) : null);
            roles.add(role);
        }

        return roles;
    }


}
