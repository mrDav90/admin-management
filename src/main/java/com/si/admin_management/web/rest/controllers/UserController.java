package com.si.admin_management.web.rest.controllers;

import com.si.admin_management.dtos.keycloak.KcUserDto;
import com.si.admin_management.dtos.keycloak.KcUserDtoRequest;
import com.si.admin_management.dtos.keycloak.UserInfos;
import com.si.admin_management.services.users.IUserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Getter
@Setter
public class UserController {
    private final IUserService userService;

    @GetMapping
    //@PreAuthorize("hasRole('admin')")
    @PreAuthorize("hasAuthority('PERMISSION_account:read')")
    public ResponseEntity<Page<KcUserDto>> getUsers(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize){
        Page<KcUserDto> users = userService.getUsers(pageNumber,pageSize);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_account:create')")
    public ResponseEntity<KcUserDto> createUser(@RequestBody KcUserDtoRequest kcUserDtoRequest) {
        KcUserDto kcUserDto = userService.createUser(kcUserDtoRequest);
        return new ResponseEntity<>(kcUserDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_account:update')")
    public ResponseEntity<Boolean> updateUser(@PathVariable("id") String id, @RequestBody KcUserDtoRequest kcUserDtoRequest) {
        userService.updateUser(id , kcUserDtoRequest);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_account:delete')")
    public ResponseEntity<Boolean> deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfos> getMe(){
        UserInfos userInfos = userService.getMe();
        return new ResponseEntity<>(userInfos, HttpStatus.OK);
    }

}
