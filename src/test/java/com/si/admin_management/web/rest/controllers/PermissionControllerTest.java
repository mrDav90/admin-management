package com.si.admin_management.web.rest.controllers;

import com.si.admin_management.dtos.keycloak.AppPermission;
import com.si.admin_management.services.roles.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PermissionControllerTest {
    @Mock
    private RoleServiceImpl roleService;
    @InjectMocks
    private PermissionController permissionController;

    private List<AppPermission> permissionsList;
    private List<String> permissionsByRoleList;


    @BeforeEach
    void setUp() {
        permissionsList = List.of(
                new AppPermission("create-account" , "create-account", List.of())
        );

        permissionsByRoleList = List.of(
                "create-account"
        );

    }

    @Test
    void testGetPermissions_ReturnsOkResponse() {
        when(roleService.getAllPermissions()).thenReturn(permissionsList);
        ResponseEntity<List<AppPermission>> response = permissionController.getPermissions();
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1,response.getBody().size());
        verify(roleService, times(1)).getAllPermissions();
    }

    @Test
    void testGetPermissionsByRole_ReturnsOkResponse() {
        when(roleService.getPermissionsByRole("admin")).thenReturn(permissionsByRoleList);
        ResponseEntity<List<String>> response = permissionController.getPermissionsByRole("admin");
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1,response.getBody().size());
        verify(roleService, times(1)).getPermissionsByRole("admin");
    }



}
