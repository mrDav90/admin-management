package com.si.admin_management.services.roles;

import com.si.admin_management.dtos.keycloak.*;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private Keycloak keycloakAdmin;

    // L'utilisation de RETURNS_DEEP_STUBS est essentielle pour mocker les API fluides comme celle de Keycloak
    // sans avoir à mocker chaque étape de la chaîne (realm, clients, roles, etc.) manuellement.
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RealmResource realmResource;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RolesResource rolesResource;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AuthorizationResource authorizationResource;


    @BeforeEach
    void setUp() {
        // Injection manuelle des valeurs des champs @Value
        ReflectionTestUtils.setField(roleService, "realm", "test-realm");
        ReflectionTestUtils.setField(roleService, "clientId", "test-client");

        // Préparation du mock de base pour la plupart des tests
        when(keycloakAdmin.realm("test-realm")).thenReturn(realmResource);
    }

    // --- Test de getClientUUID ---

    @Test
    @DisplayName("getClientUUID devrait retourner l'UUID du client quand il est trouvé")
    void getClientUUID_shouldReturnId_whenClientExists() {
        // GIVEN
        String expectedUuid = "client-uuid-123";
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setId(expectedUuid);

        when(realmResource.clients().findByClientId("test-client")).thenReturn(List.of(clientRepresentation));

        // WHEN
        String actualUuid = roleService.getClientUUID();

        // THEN
        assertEquals(expectedUuid, actualUuid);
    }

    @Test
    @DisplayName("getClientUUID devrait retourner une chaîne vide quand le client n'est pas trouvé")
    void getClientUUID_shouldReturnEmptyString_whenClientDoesNotExist() {
        // GIVEN
        when(realmResource.clients().findByClientId("test-client")).thenReturn(Collections.emptyList());

        // WHEN
        String actualUuid = roleService.getClientUUID();

        // THEN
        assertThat(actualUuid).isEmpty();
    }

    // --- Test de createRole et createPolicy ---

    @Test
    @DisplayName("createRole devrait créer un rôle et une policy associée avec succès")
    void createRole_shouldCreateRoleAndPolicySuccessfully() {
        // GIVEN
        KcRoleDtoRequest request = new KcRoleDtoRequest("new-role", "A new test role");

        RoleRepresentation createdRole = new RoleRepresentation();
        createdRole.setId("role-id-123");
        createdRole.setName(request.getName());
        createdRole.setDescription(request.getDescription());

        // Mock pour la partie création de rôle
        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get(request.getName()).toRepresentation()).thenReturn(createdRole);

        // Mock pour la partie création de policy (qui est appelée à l'intérieur)
        String clientUuid = "client-uuid-123";
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setId(clientUuid);
        when(realmResource.clients().findByClientId("test-client")).thenReturn(List.of(clientRepresentation));
        when(realmResource.clients().get(clientUuid).authorization()).thenReturn(authorizationResource);

        Response mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(201); // 201 = Created
        when(authorizationResource.policies().role().create(any(RolePolicyRepresentation.class))).thenReturn(mockResponse);

        // WHEN
        KcRoleDto result = roleService.createRole(request);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(createdRole.getId());
        assertThat(result.getName()).isEqualTo(createdRole.getName());

        // On peut aussi vérifier que la méthode create de Keycloak a été appelée avec les bonnes données
        verify(rolesResource).create(any(RoleRepresentation.class));
        verify(authorizationResource.policies().role()).create(any(RolePolicyRepresentation.class));
    }

    @Test
    @DisplayName("createRole devrait gérer une erreur de création de policy")
    void createRole_shouldHandlePolicyCreationError() {
        // GIVEN
        KcRoleDtoRequest request = new KcRoleDtoRequest("error-role", "A role that fails policy creation");

        RoleRepresentation createdRole = new RoleRepresentation();
        createdRole.setId("role-id-456");
        createdRole.setName(request.getName());

        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get(request.getName()).toRepresentation()).thenReturn(createdRole);

        String clientUuid = "client-uuid-123";
        when(realmResource.clients().findByClientId("test-client")).thenReturn(List.of(clientWithId(clientUuid)));
        when(realmResource.clients().get(clientUuid).authorization()).thenReturn(authorizationResource);

        Response mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(400); // 400 = Bad Request
        when(mockResponse.getStatusInfo()).thenReturn(Response.Status.BAD_REQUEST); // Pour couvrir la ligne de log
        when(authorizationResource.policies().role().create(any(RolePolicyRepresentation.class))).thenReturn(mockResponse);

        // WHEN
        KcRoleDto result = roleService.createRole(request);

        // THEN
        assertThat(result).isNotNull();
        verify(authorizationResource.policies().role()).create(any(RolePolicyRepresentation.class));
    }

    // --- Test de getRoles ---

    @Test
    @DisplayName("getRoles devrait retourner une page de rôles")
    void getRoles_shouldReturnPagedRoles() {
        // GIVEN
        RoleRepresentation role1 = new RoleRepresentation("role1", "desc1", false);
        role1.setId("id1");
        RoleRepresentation role2 = new RoleRepresentation("role2", "desc2", false);
        role2.setId("id2");

        List<RoleRepresentation> pagedList = List.of(role1, role2);
        List<RoleRepresentation> fullList = List.of(role1, role2, new RoleRepresentation()); // Total de 3

        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.list(0, 10)).thenReturn(pagedList);
        when(rolesResource.list()).thenReturn(fullList);

        // WHEN
        var resultPage = roleService.getRoles(0, 10);

        // THEN
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(2);
        assertThat(resultPage.getContent()).hasSize(2);
        assertThat(resultPage.getContent().get(0).getName()).isEqualTo("role1");
    }

    // --- Test de getAllPermissions ---

    @Test
    @DisplayName("getAllPermissions devrait retourner les ressources et leurs permissions associées")
    void getAllPermissions_shouldReturnResourcesAndPermissions() {
        // GIVEN
        String clientUuid = "client-uuid-123";
        when(realmResource.clients().findByClientId("test-client")).thenReturn(List.of(clientWithId(clientUuid)));
        when(realmResource.clients().get(clientUuid).authorization()).thenReturn(authorizationResource);

        // Mock des ressources
        ResourceRepresentation resource1 = new ResourceRepresentation("resource-a", Set.of());
        resource1.setId("res-id-a");
        resource1.setDisplayName("Resource A");
        when(authorizationResource.resources().resources()).thenReturn(List.of(resource1));

        // Mock des policies pour cette ressource
        PolicyRepresentation policy1 = new PolicyRepresentation();
        policy1.setName("permission-view-a");
        policy1.setDescription("Can view Resource A");
        when(authorizationResource.policies().policies(null, null, null, "res-id-a", null, true, null, null, null, null))
                .thenReturn(List.of(policy1));

        // WHEN
        List<com.si.admin_management.dtos.keycloak.AppPermission> permissions = roleService.getAllPermissions();

        // THEN
        assertThat(permissions).hasSize(1);
        var appPermission = permissions.get(0);
        assertThat(appPermission.getResourceName()).isEqualTo("resource-a");
        assertThat(appPermission.getResourceDisplayName()).isEqualTo("Resource A");
        assertThat(appPermission.getPermissions()).hasSize(1);
        assertThat(appPermission.getPermissions().get(0).getName()).isEqualTo("permission-view-a");
    }

    // --- Test de getPermissionsByRole ---

    @Test
    @DisplayName("getPermissionsByRole devrait retourner les permissions d'un rôle")
    void getPermissionsByRole_shouldReturnPermissionsForRole() {
        // GIVEN
        String roleName = "test-role";
        String clientUuid = "client-uuid-123";
        when(realmResource.clients().findByClientId("test-client")).thenReturn(List.of(clientWithId(clientUuid)));
        when(realmResource.clients().get(clientUuid).authorization()).thenReturn(authorizationResource);

        RolePolicyRepresentation rolePolicy = new RolePolicyRepresentation();
        rolePolicy.setId("policy-id-for-role");
        when(authorizationResource.policies().role().findByName(roleName)).thenReturn(rolePolicy);

        PolicyRepresentation dependentPolicy = new PolicyRepresentation();
        dependentPolicy.setName("dependent-permission-name");
        when(authorizationResource.policies().policy("policy-id-for-role").dependentPolicies()).thenReturn(List.of(dependentPolicy));

        // WHEN
        List<String> permissions = roleService.getPermissionsByRole(roleName);

        // THEN
        assertThat(permissions).containsExactly("dependent-permission-name");
    }

    // --- Test de assignPermissionsToRole ---

    @Test
    @DisplayName("assignPermissionsToRole devrait assigner une nouvelle permission")
    void assignPermissionsToRole_shouldAssignNewPermission() {
        // GIVEN
        String roleName = "test-role";
        String permissionName = "new-permission";
        String permissionDescription = "existing-permission description";
        var request = new AssignPermissionRequestDto(List.of(new PermissionItem(permissionName , permissionDescription)));

        String clientUuid = "client-uuid-123";
        when(realmResource.clients().findByClientId("test-client")).thenReturn(List.of(clientWithId(clientUuid)));
        when(realmResource.clients().get(clientUuid).authorization()).thenReturn(authorizationResource);

        // Mocks pour la permission
        ScopePermissionRepresentation scopePerm = new ScopePermissionRepresentation();
        scopePerm.setId("perm-id");
        scopePerm.setName(permissionName);
        when(authorizationResource.permissions().scope().findByName(permissionName)).thenReturn(scopePerm);

        // Mocks pour la policy du rôle
        PolicyRepresentation rolePolicy = new PolicyRepresentation();
        rolePolicy.setId("role-policy-id");
        when(authorizationResource.policies().findByName(roleName)).thenReturn(rolePolicy);

        // Mock des policies déjà associées (ici, aucune)
        when(authorizationResource.permissions().scope().findById("perm-id").associatedPolicies()).thenReturn(List.of());

        // WHEN
        roleService.assignPermissionsToRole(roleName, request);

        // THEN
        // On vérifie que la mise à jour est bien appelée
        ArgumentCaptor<ScopePermissionRepresentation> captor = ArgumentCaptor.forClass(ScopePermissionRepresentation.class);
        verify(authorizationResource.permissions().scope().findById("perm-id")).update(captor.capture());

        // On vérifie que la policy a bien été ajoutée
        assertThat(captor.getValue().getPolicies()).contains("role-policy-id");
        assertThat(captor.getValue().getDecisionStrategy()).isEqualTo(DecisionStrategy.AFFIRMATIVE);
    }

    @Test
    @DisplayName("assignPermissionsToRole ne devrait rien faire si la permission est déjà assignée")
    void assignPermissionsToRole_shouldDoNothingIfPermissionIsAlreadyAssigned() {
        // GIVEN
        String roleName = "test-role";
        String permissionName = "existing-permission";
        String permissionDescription = "existing-permission description";
        var request = new AssignPermissionRequestDto(List.of(new PermissionItem(permissionName , permissionDescription)));

        String clientUuid = "client-uuid-123";
        when(realmResource.clients().findByClientId("test-client")).thenReturn(List.of(clientWithId(clientUuid)));
        when(realmResource.clients().get(clientUuid).authorization()).thenReturn(authorizationResource);

        ScopePermissionRepresentation scopePerm = new ScopePermissionRepresentation();
        scopePerm.setId("perm-id");
        when(authorizationResource.permissions().scope().findByName(permissionName)).thenReturn(scopePerm);

        PolicyRepresentation rolePolicy = new PolicyRepresentation();
        rolePolicy.setId("role-policy-id");
        when(authorizationResource.policies().findByName(roleName)).thenReturn(rolePolicy);

        // Mock des policies déjà associées (la policy du rôle est déjà là)
        //AbstractPolicyRepresentation existingAssociatedPolicy = new PolicyRepresentation();
        PolicyRepresentation existingAssociatedPolicy = new PolicyRepresentation();
        existingAssociatedPolicy.setName("role-policy-id"); // NOTE : le test d'appartenance se fait sur le NOM !
        when(authorizationResource.permissions().scope().findById("perm-id").associatedPolicies()).thenReturn(List.of(existingAssociatedPolicy));

        // WHEN
        roleService.assignPermissionsToRole(roleName, request);

        // THEN
        // On vérifie que la mise à jour n'a JAMAIS été appelée
        verify(authorizationResource.permissions().scope().findById("perm-id"), never()).update(any());
    }

    // --- Helper Method ---
    private ClientRepresentation clientWithId(String uuid) {
        ClientRepresentation client = new ClientRepresentation();
        client.setId(uuid);
        return client;
    }
}