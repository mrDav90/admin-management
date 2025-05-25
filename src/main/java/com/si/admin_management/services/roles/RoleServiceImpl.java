package com.si.admin_management.services.roles;

import com.si.admin_management.dtos.keycloak.*;
import com.si.admin_management.services.users.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final Keycloak  keycloakAdmin;

    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;


    public KcRoleDto createRole(KcRoleDtoRequest kcRoleDtoRequest) {

        RolesResource rolesResource = keycloakAdmin.realm(realm).roles();

        RoleRepresentation role = new RoleRepresentation();
        role.setName(kcRoleDtoRequest.getName());
        role.setDescription(kcRoleDtoRequest.getDescription());
        rolesResource.create(role);

        RoleRepresentation createdRole = rolesResource.get(kcRoleDtoRequest.getName()).toRepresentation();
        String policyName = createdRole.getName();
        createPolicy(policyName);

        KcRoleDto kcRoleDto = new KcRoleDto();
        kcRoleDto.setId(createdRole.getId());
        kcRoleDto.setName(createdRole.getName());
        kcRoleDto.setDescription(createdRole.getDescription());

        return kcRoleDto;
    }

    private void createPolicy(String roleName) {
        RolePolicyRepresentation rolePolicy = new RolePolicyRepresentation();
        rolePolicy.setName(roleName);
        rolePolicy.setDescription("Policy for role"+roleName);
        rolePolicy.setType("role");
        rolePolicy.setLogic(Logic.POSITIVE);

        Set<RolePolicyRepresentation.RoleDefinition> roles = new HashSet<>();
        RolePolicyRepresentation.RoleDefinition roleDef = new RolePolicyRepresentation.RoleDefinition();
        roleDef.setId(roleName);
        roleDef.setRequired(true);
        roles.add(roleDef);

        rolePolicy.setRoles(roles);
        var policyCreated = keycloakAdmin.realm(realm).clients().get(getClientUUID()).authorization().policies().role().create(rolePolicy);
        if (policyCreated.getStatus() == 201){
            logger.info("Policy created");
        }else {
            logger.info("Policy creation error");
            logger.info(policyCreated.getStatusInfo().toString());
        }
    }

    public Page<KcRoleDto> getRoles(int pageNumber, int pageSize) {

        RolesResource roleResource = keycloakAdmin.realm(realm).roles();
        int firstResult = pageNumber * pageSize;
        //roleResource.list();
        List<RoleRepresentation> roleRepresentation = roleResource.list(firstResult,pageSize);
        Pageable pagedRequest = PageRequest.of(pageNumber,pageSize);
        List<KcRoleDto> roles = roleRepresentation.stream()
//                .filter(item->
//                        //item.getName().startsWith("app_");
//                        !isKeycloakImplicitRole(item.getName())
//                )
                .map(role -> {
            KcRoleDto kcRoleDto = new KcRoleDto();
            kcRoleDto.setId(role.getId());
            kcRoleDto.setName(role.getName());
            kcRoleDto.setDescription(role.getDescription());
            return kcRoleDto;
        }).toList() ;
        long totalRoles = roleResource.list().size();
        return new PageImpl<>(roles, pagedRequest, totalRoles) ;
    }

    private boolean isKeycloakImplicitRole(String roleName) {
        List<String> implicitRolePatterns = Arrays.asList(
                "default-roles",
                "offline_access",
                "uma_authorization"
        );

        return implicitRolePatterns.stream()
                .anyMatch(pattern -> roleName.startsWith(pattern) || roleName.equals(pattern));
    }

    public List<AppPermission> getAllPermissions () {
        AuthorizationResource authorization = keycloakAdmin.realm(realm).clients().get(getClientUUID()).authorization();

         return authorization.resources().resources().stream().map(item -> {
            AppPermission ap = new AppPermission();
            ap.setResourceName(item.getName());
            ap.setResourceDisplayName(item.getDisplayName());
            ap.setPermissions(
                    authorization.policies().policies(null, null, null, item.getId(), null, true, null, null, null, null)
                    .stream()
                    .map(elt -> {
                        PermissionItem permissionItem = new PermissionItem();
                        permissionItem.setName(elt.getName());
                        permissionItem.setDescription(elt.getDescription());
                        return permissionItem;
                    }).toList()
            );
            return ap;
        }).toList();

//        return authorization.policies()
//                .policies(null, null, null, null, null, true, null, null, null, null)
//                .stream()
//                .map(item -> {
//                    AppPermission ap = new AppPermission();
//                    ap.setName(item.getName());
//                    ap.setDescription(item.getDescription());
//                    return ap;
//                })
//                .toList();
    }


    public List<String> getPermissionsByRole (String roleName) {
        AuthorizationResource authorization = keycloakAdmin.realm(realm).clients().get(getClientUUID()).authorization();
        RolePolicyRepresentation policy = authorization.policies().role().findByName(roleName);
        String policyId = policy.getId();
        return authorization.policies().policy(policyId).dependentPolicies().stream().map(AbstractPolicyRepresentation::getName).collect(Collectors.toList());
    }

    public void assignPermissionsToRole(String roleName, AssignPermissionRequestDto assignPermissionRequestDto) {
        AuthorizationResource authorization = keycloakAdmin.realm(realm).clients().get(getClientUUID()).authorization();
        String policyName = roleName ;

        assignPermissionRequestDto.getPermissions().forEach(permission -> {
            ScopePermissionRepresentation scopePermissionRepresentation = authorization.permissions().scope().findByName(permission.getName());
            PolicyRepresentation policy = authorization.policies().findByName(policyName);
            String policyId = policy.getId();

            Set<String> currentPolicies = new HashSet<>();

            authorization.permissions().scope().findById(scopePermissionRepresentation.getId()).associatedPolicies().forEach(item -> {
                currentPolicies.add(item.getName());
            });

            if (!currentPolicies.contains(policyId)) {
                currentPolicies.add(policyId);
                scopePermissionRepresentation.setPolicies(currentPolicies);
                scopePermissionRepresentation.setDecisionStrategy(DecisionStrategy.AFFIRMATIVE);
                authorization.permissions().scope().findById(scopePermissionRepresentation.getId()).update(scopePermissionRepresentation);
            }

        });

    }

    private String getClientUUID () {
        String clientUUID = "";
        List<ClientRepresentation> clients = keycloakAdmin.realm(realm).clients().findByClientId(clientId);
        if(!clients.isEmpty()) {
            ClientRepresentation client = clients.get(0);
            clientUUID = client.getId();
        }
        //logger.info("Id : {}", clientUUID);
        return clientUUID;
    }
}
