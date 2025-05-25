package com.si.admin_management.web.rest.controllers;

import com.si.admin_management.dtos.keycloak.AppPermission;
import com.si.admin_management.services.roles.IRoleService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Getter
@Setter
public class PermissionController {
    private final IRoleService roleService;
    @GetMapping
    //@PreAuthorize("hasRole('admin')")
    @PreAuthorize("hasAuthority('PERMISSION_permission:read')")
    public ResponseEntity<List<AppPermission>> getPermissions() {
        List<AppPermission> permissions = roleService.getAllPermissions();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }

    @GetMapping("/{roleName}")
    @PreAuthorize("hasAuthority('PERMISSION_permission:read')")
    public ResponseEntity<List<String>> getPermissionsByRole(@PathVariable("roleName") String roleName) {
        List<String> permissions = roleService.getPermissionsByRole(roleName);
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }
}
