package com.si.admin_management.dtos.keycloak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class KcRoleDto {
    private String id;
    private String name;
    private String description;
}