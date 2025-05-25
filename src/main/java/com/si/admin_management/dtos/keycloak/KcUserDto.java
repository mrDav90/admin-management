package com.si.admin_management.dtos.keycloak;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KcUserDto {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean enabled;
    private List<String> realmRoles;
}