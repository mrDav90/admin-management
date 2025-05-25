package com.si.admin_management.dtos.keycloak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    private String rsid;
    private String rsname;
    private Set<String> scopes;
}