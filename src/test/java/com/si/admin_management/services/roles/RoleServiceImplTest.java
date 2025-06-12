package com.si.admin_management.services.roles;

import com.si.admin_management.dtos.keycloak.*;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
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


    @Mock private ClientsResource clientsResource;
    @Mock private ClientResource clientResource;
    @Mock private AuthorizationResource authorizationResource;
    @Mock private PoliciesResource policiesResource;
    @Mock private RolePoliciesResource rolePoliciesResource;
    @Mock private PolicyResource policyResource;
    @Mock private PermissionsResource permissionsResource;
    @Mock private ScopePermissionsResource scopePermissionsResource;
    @Mock private ScopePermissionResource scopePermissionResource;
    @Mock private ResourcesResource resourcesResource;

    // --- Mock pour la réponse HTTP ---
    @Mock private Response mockWsResponse;
    @Mock private Response.StatusType mockStatusInfo;

    @Captor
    private ArgumentCaptor<RolePolicyRepresentation> rolePolicyCaptor;
    @Captor private ArgumentCaptor<ScopePermissionRepresentation> scopePermissionCaptor;

    @InjectMocks
    private RoleServiceImpl roleService;

    private static final String realm = "test-realm";
    private static final String clientId = "test-client";
    private static final String client_uuid = "a1b2c3d4-e5f6-7890-1234-567890abcdef";

    @BeforeEach
    void setUp() {
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        when(realmResource.roles()).thenReturn(rolesResource);

        roleService = new RoleServiceImpl(keycloakAdmin);
        ReflectionTestUtils.setField(roleService, "realm", realm);
        ReflectionTestUtils.setField(roleService, "clientId", clientId);

        // Configuration de la chaîne de mocks principale, utilisée par presque toutes les méthodes
        when(keycloakAdmin.realm(realm)).thenReturn(realmResource);
        //when(realmResource.clients()).thenReturn(clientsResource);

        // Mock pour la méthode getClientUUID()
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setId(client_uuid);
        //when(clientsResource.findByClientId(clientId)).thenReturn(Collections.singletonList(clientRepresentation));

        // Suite de la chaîne de mocks
        //when(clientsResource.get(client_uuid)).thenReturn(clientResource);
        //when(clientResource.authorization()).thenReturn(authorizationResource);
//        when(authorizationResource.policies()).thenReturn(policiesResource);
//        when(authorizationResource.permissions()).thenReturn(permissionsResource);
//        when(authorizationResource.resources()).thenReturn(resourcesResource);

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


//    @Nested
//    class GetClientUUIDTests {
//        @Test
//        void getClientUUID_whenClientFound_returnsId() {
//            String result = roleService.getClientUUID();
//            assertThat(result).isEqualTo(client_uuid);
//        }
//
//        @Test
//        void getClientUUID_whenClientNotFound_returnsEmptyString() {
//            when(clientsResource.findByClientId(client_uuid)).thenReturn(Collections.emptyList());
//            String result = roleService.getClientUUID();
//            assertThat(result).isEmpty();
//        }
//    }
//
//    @Nested
//    class CreatePolicyTests {
//        @BeforeEach
//        void setUpPolicyMocks() {
//            when(policiesResource.role()).thenReturn(rolePoliciesResource);
//        }
//
//        @Test
//        void createPolicy_whenCreationSucceeds_logsSuccess() {
//            // Given: Le serveur retourne un statut 201 (Created)
//            when(mockWsResponse.getStatus()).thenReturn(201);
//            when(rolePoliciesResource.create(any(RolePolicyRepresentation.class))).thenReturn(mockWsResponse);
//
//            // When
//            roleService.createPolicy("test-role");
//
//            // Then
//            verify(rolePoliciesResource).create(rolePolicyCaptor.capture());
//            RolePolicyRepresentation capturedPolicy = rolePolicyCaptor.getValue();
//
//            assertThat(capturedPolicy.getName()).isEqualTo("test-role");
//            assertThat(capturedPolicy.getType()).isEqualTo("role");
//            assertThat(capturedPolicy.getLogic()).isEqualTo(Logic.POSITIVE);
//            assertThat(capturedPolicy.getRoles()).hasSize(1);
//            RolePolicyRepresentation.RoleDefinition roleDef = capturedPolicy.getRoles().iterator().next();
//            assertThat(roleDef.getId()).isEqualTo("test-role");
//            assertThat(roleDef.isRequired()).isTrue();
//        }
//
//        @Test
//        void createPolicy_whenCreationFails_logsError() {
//            // Given: Le serveur retourne une erreur (ex: 400 Bad Request)
//            when(mockWsResponse.getStatus()).thenReturn(400);
//            when(mockWsResponse.getStatusInfo()).thenReturn(mockStatusInfo);
//            when(mockStatusInfo.toString()).thenReturn("Bad Request");
//            when(rolePoliciesResource.create(any(RolePolicyRepresentation.class))).thenReturn(mockWsResponse);
//
//            // When
//            roleService.createPolicy("test-role");
//
//            // Then
//            verify(rolePoliciesResource).create(any(RolePolicyRepresentation.class));
//            verify(mockWsResponse, times(2)).getStatusInfo(); // Appelé une fois pour le log
//        }
//    }
//
//    @Nested
//    class GetAllPermissionsTests {
//        @Test
//        void getAllPermissions_mapsResourcesAndPoliciesCorrectly() {
//            // Given: Mocks pour les ressources et les policies associées
//            ResourceRepresentation resource1 = new ResourceRepresentation("resource-un", new HashSet<>(), "Resource Un", null);
//            resource1.setId("id-res-1");
//            ResourceRepresentation resource2 = new ResourceRepresentation("resource-deux", new HashSet<>(), "Resource Deux", null);
//            resource2.setId("id-res-2");
//
//            PolicyRepresentation policy1 = new PolicyRepresentation();
//            policy1.setName("permission-A");
//            policy1.setDescription("Desc A");
//
//            PolicyRepresentation policy2 = new PolicyRepresentation();
//            policy2.setName("permission-B");
//            policy2.setDescription("Desc B");
//
//            when(resourcesResource.resources()).thenReturn(List.of(resource1, resource2));
//            when(policiesResource.policies(null, null, null, "id-res-1", null, true, null, null, null, null))
//                    .thenReturn(List.of(policy1));
//            when(policiesResource.policies(null, null, null, "id-res-2", null, true, null, null, null, null))
//                    .thenReturn(List.of(policy2));
//
//            // When
//            List<AppPermission> result = roleService.getAllPermissions();
//
//            // Then
//            assertThat(result).hasSize(2);
//            AppPermission p1 = result.get(0);
//            assertThat(p1.getResourceName()).isEqualTo("resource-un");
//            assertThat(p1.getResourceDisplayName()).isEqualTo("Resource Un");
//            assertThat(p1.getPermissions()).hasSize(1);
//            assertThat(p1.getPermissions().get(0).getName()).isEqualTo("permission-A");
//
//            AppPermission p2 = result.get(1);
//            assertThat(p2.getResourceName()).isEqualTo("resource-deux");
//            assertThat(p2.getPermissions()).hasSize(1);
//        }
//    }
//
//    @Nested
//    class GetPermissionsByRoleTests {
//        @Test
//        void getPermissionsByRole_returnsDependentPolicyNames() {
//            // Given
//            RolePolicyRepresentation rolePolicy = new RolePolicyRepresentation();
//            rolePolicy.setId("policy-id-for-role");
//
//            PolicyRepresentation dependentPolicy1 = new PolicyRepresentation();
//            dependentPolicy1.setName("Dependent-Policy-1");
//            PolicyRepresentation dependentPolicy2 = new PolicyRepresentation();
//            dependentPolicy2.setName("Dependent-Policy-2");
//
//            when(policiesResource.role()).thenReturn(rolePoliciesResource);
//            when(rolePoliciesResource.findByName("admin-role")).thenReturn(rolePolicy);
//            when(policiesResource.policy("policy-id-for-role")).thenReturn(policyResource);
//            when(policyResource.dependentPolicies()).thenReturn(List.of(dependentPolicy1, dependentPolicy2));
//
//            // When
//            List<String> result = roleService.getPermissionsByRole("admin-role");
//
//            // Then
//            assertThat(result).containsExactlyInAnyOrder("Dependent-Policy-1", "Dependent-Policy-2");
//        }
//    }
//
//    @Nested
//    class AssignPermissionsToRoleTests {
//
//        @BeforeEach
//        void setUpAssignMocks() {
//            when(permissionsResource.scope()).thenReturn(scopePermissionsResource);
//            when(policiesResource.findByName(anyString())).thenReturn(new PolicyRepresentation() {{ setId("policy-id-123"); }});
//        }
//
//        @Test
//        void assignPermissionsToRole_whenPermissionNotAssociated_updatesPolicy() {
//            // Given
//            String roleName = "editor-role";
//            AssignPermissionRequestDto request = new AssignPermissionRequestDto();
//            PermissionItem permissionItem = new PermissionItem("edit-article" , "Edit article");
//            request.setPermissions(Collections.singletonList(permissionItem));
//
//            ScopePermissionRepresentation scopePerm = new ScopePermissionRepresentation();
//            scopePerm.setId("scope-perm-id-456");
//
//            when(scopePermissionsResource.findByName("edit-article")).thenReturn(scopePerm);
//            when(scopePermissionsResource.findById("scope-perm-id-456")).thenReturn(scopePermissionResource);
//            // Simule qu'il n'y a pas de policy associée pour l'instant
//            when(scopePermissionResource.associatedPolicies()).thenReturn((List<PolicyRepresentation>) Stream.empty());
//
//            // When
//            roleService.assignPermissionsToRole(roleName, request);
//
//            // Then
//            verify(scopePermissionResource).update(scopePermissionCaptor.capture());
//            ScopePermissionRepresentation captured = scopePermissionCaptor.getValue();
//            assertThat(captured.getPolicies()).contains("policy-id-123");
//            assertThat(captured.getDecisionStrategy()).isEqualTo(DecisionStrategy.AFFIRMATIVE);
//        }
//
//        @Test
//        void assignPermissionsToRole_whenPermissionAlreadyAssociated_doesNotUpdate() {
//            // Given
//            String roleName = "editor-role";
//            AssignPermissionRequestDto request = new AssignPermissionRequestDto();
//            PermissionItem permissionItem = new PermissionItem("edit-article" , "Edit article");
//            request.setPermissions(Collections.singletonList(permissionItem));
//
//            ScopePermissionRepresentation scopePerm = new ScopePermissionRepresentation();
//            scopePerm.setId("scope-perm-id-456");
//
//            // Simule que la policy est déjà associée
//            PolicyRepresentation existingPolicy = new PolicyRepresentation();
//            existingPolicy.setName("policy-id-123"); // Important: le test logique se base sur l'ID/Nom
//
//            when(scopePermissionsResource.findByName("edit-article")).thenReturn(scopePerm);
//            when(scopePermissionsResource.findById("scope-perm-id-456")).thenReturn(scopePermissionResource);
//            when(scopePermissionResource.associatedPolicies()).thenReturn(List.of(existingPolicy));
//
//            // When
//            roleService.assignPermissionsToRole(roleName, request);
//
//            // Then
//            // On vérifie que la méthode update n'a JAMAIS été appelée
//            verify(scopePermissionResource, never()).update(any());
//        }
//    }


}
