package com.si.admin_management.web.rest.controllers;

import com.si.admin_management.dtos.keycloak.AssignPermissionRequestDto;
import com.si.admin_management.dtos.keycloak.KcRoleDto;
import com.si.admin_management.dtos.keycloak.KcRoleDtoRequest;
import com.si.admin_management.services.roles.IRoleService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Getter
@Setter
public class RoleController {
    private final IRoleService roleService;

    @GetMapping
    //@PreAuthorize("hasRole('admin')")
    @PreAuthorize("hasAuthority('PERMISSION_app_role:read')")
    public ResponseEntity<Page<KcRoleDto>> getRoles(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
        Page<KcRoleDto> roles = roleService.getRoles(pageNumber, pageSize);
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_app_role:create')")
    public ResponseEntity<KcRoleDto> createRole(@RequestBody KcRoleDtoRequest kcRoleDtoRequest) {
        KcRoleDto role = roleService.createRole(kcRoleDtoRequest);
        return new ResponseEntity<>(role, HttpStatus.CREATED);
    }

    @PutMapping("/{roleName}/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_permission:assign')")
    public ResponseEntity<?> assignPermissionsToRole( @PathVariable("roleName") String roleName, @RequestBody AssignPermissionRequestDto assignPermissionRequestDto) {
        roleService.assignPermissionsToRole(roleName, assignPermissionRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
