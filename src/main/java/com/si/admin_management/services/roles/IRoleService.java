package com.si.admin_management.services.roles;

import com.si.admin_management.dtos.keycloak.AppPermission;
import com.si.admin_management.dtos.keycloak.AssignPermissionRequestDto;
import com.si.admin_management.dtos.keycloak.KcRoleDto;
import com.si.admin_management.dtos.keycloak.KcRoleDtoRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IRoleService {
    KcRoleDto createRole(KcRoleDtoRequest kcRoleDtoRequest);
    Page<KcRoleDto> getRoles(int pageNumber, int pageSize);
    List<AppPermission> getAllPermissions ();
    void assignPermissionsToRole(String roleName, AssignPermissionRequestDto assignPermissionRequestDto);
    List<String> getPermissionsByRole (String roleName);
}
