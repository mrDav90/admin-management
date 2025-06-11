package com.si.admin_management.web.rest.controllers;

import com.si.admin_management.dtos.keycloak.KcUserDto;
import com.si.admin_management.dtos.keycloak.KcUserDtoRequest;
import com.si.admin_management.dtos.keycloak.UserInfos;
import com.si.admin_management.services.users.UserServiceImpl;
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
public class UserControllerTest {
    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private UserController userController;

    private List<KcUserDto> usersList;
    KcUserDtoRequest userDtoRequest = new KcUserDtoRequest();
    UserInfos userInfos = new UserInfos();
    @BeforeEach
    void setUp() {
        usersList = List.of(
                new KcUserDto("1", "lucas", "Lucas", "Bernard", "lucas@gmail.com", true, List.of())
        );

        userDtoRequest.setFirstName("Lucas");
        userDtoRequest.setLastName("Bernard");
        userDtoRequest.setEmail("lucas@gmail.com");
        userDtoRequest.setRole("ADMIN");
        userDtoRequest.setPassword("passer");

        userInfos.setFirstName("Lucas");
        userInfos.setLastName("Bernard");
        userInfos.setEmail("lucas@gmail.com");
        userInfos.setRole("ADMIN");
        userInfos.setPermissions(List.of());
    }

    @Test
    void testGetPaginatedUsers_ReturnsOkResponse() {
        Page<KcUserDto> page = new PageImpl<>(usersList);
        when(userService.getUsers(0,1)).thenReturn(page);
        ResponseEntity<Page<KcUserDto>> response = userController.getUsers(0,1);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1,response.getBody().getTotalElements());
        assertEquals(0 , response.getBody().getNumber());
        verify(userService, times(1)).getUsers(0,1);
    }

    @Test
    void testGetPaginatedUsers_ReturnsEmptyList() {
        Page<KcUserDto> page = new PageImpl<>(List.of());
        when(userService.getUsers(0,1)).thenReturn(page);
        ResponseEntity<Page<KcUserDto>> response = userController.getUsers(0,1);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(userService, times(1)).getUsers(0,1);
    }

    @Test
    void testGetMe_ReturnsOkResponse() {
        when(userService.getMe()).thenReturn(userInfos);
        ResponseEntity<UserInfos> response = userController.getMe();
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userInfos,response.getBody());
        verify(userService, times(1)).getMe();
    }

    @Test
    void testCreateUser_ReturnsOkResponse() {
        when(userService.createUser(userDtoRequest)).thenReturn(usersList.get(0));
        ResponseEntity<KcUserDto> response = userController.createUser(userDtoRequest);
        assertEquals(HttpStatus.CREATED , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usersList.get(0),response.getBody());
        verify(userService, times(1)).createUser(userDtoRequest);
    }

    @Test
    void testUpdateUser_ReturnsOkResponse() {
        doNothing().when(userService).updateUser("1", userDtoRequest);
        ResponseEntity<Boolean> response = userController.updateUser("1", userDtoRequest);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true,response.getBody());
        verify(userService, times(1)).updateUser("1",userDtoRequest);
    }

    @Test
    void testDeleteUser_ReturnsOkResponse() {
        doNothing().when(userService).deleteUser("1");
        ResponseEntity<Boolean> response = userController.deleteUser("1");
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Boolean.TRUE,response.getBody());
        verify(userService, times(1)).deleteUser("1");
    }
}
