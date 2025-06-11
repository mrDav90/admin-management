package com.si.admin_management.web.rest.controllers;


import com.si.admin_management.dtos.keycloak.*;
import com.si.admin_management.services.roles.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class RoleControllerTest {
    @Mock
    private RoleServiceImpl roleService;
    @InjectMocks
    private RoleController roleController;

    private List<KcRoleDto> rolesList;
    KcRoleDtoRequest roleDtoRequest = new KcRoleDtoRequest();
    AssignPermissionRequestDto assignPermissionRequestDto = new AssignPermissionRequestDto();

    @BeforeEach
    void setUp() {
        rolesList = List.of(
                new KcRoleDto("1" , "ADMIN", "Admin role")
        );

        roleDtoRequest.setName("ADMIN");
        roleDtoRequest.setDescription("Admin role");

        assignPermissionRequestDto.setPermissions(List.of());

    }

    @Test
    void testGetPaginatedRoles_ReturnsOkResponse() {
        Page<KcRoleDto> page = new PageImpl<>(rolesList);
        when(roleService.getRoles(0,1)).thenReturn(page);
        ResponseEntity<Page<KcRoleDto>> response = roleController.getRoles(0,1);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1,response.getBody().getTotalElements());
        assertEquals(0 , response.getBody().getNumber());
        verify(roleService, times(1)).getRoles(0,1);
    }

    @Test
    void testGetPaginatedRoles_ReturnsEmptyList() {
        Page<KcRoleDto> page = new PageImpl<>(List.of());
        when(roleService.getRoles(0,1)).thenReturn(page);
        ResponseEntity<Page<KcRoleDto>> response = roleController.getRoles(0,1);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(roleService, times(1)).getRoles(0,1);
    }


    @Test
    void testCreateRole_ReturnsOkResponse() {
        when(roleService.createRole(roleDtoRequest)).thenReturn(rolesList.get(0));
        ResponseEntity<KcRoleDto> response = roleController.createRole(roleDtoRequest);
        assertEquals(HttpStatus.CREATED , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(rolesList.get(0),response.getBody());
        verify(roleService, times(1)).createRole(roleDtoRequest);
    }

    @Test
    void testAssignPermissions_ReturnsOkResponse() {
        doNothing().when(roleService).assignPermissionsToRole("1", assignPermissionRequestDto);
        ResponseEntity<?> response = roleController.assignPermissionsToRole("1", assignPermissionRequestDto);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        verify(roleService, times(1)).assignPermissionsToRole("1",assignPermissionRequestDto);
    }


}
