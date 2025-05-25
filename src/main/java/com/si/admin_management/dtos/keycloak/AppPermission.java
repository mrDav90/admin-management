package com.si.admin_management.dtos.keycloak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppPermission {

    private String resourceName;
    private String resourceDisplayName;
    private List<PermissionItem> permissions;
}