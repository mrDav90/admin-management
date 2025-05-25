package com.si.admin_management.dtos.doctors;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DoctorDtoResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
    private String specialty;
    private String registrationNumber;
}
