package com.si.admin_management.dtos.keycloak;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KcRoleDtoRequest {
    @Column(nullable = false)
    private String name;
    private String description;
}
