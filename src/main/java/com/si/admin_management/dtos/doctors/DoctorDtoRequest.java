package com.si.admin_management.dtos.doctors;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DoctorDtoRequest {
    @NotBlank(message = "Firstname is required!")
    private String firstName;

    @NotBlank(message = "Lastname is required!")
    private String lastName;

    @NotBlank(message = "Email is required!")
    private String email;

    @NotBlank(message = "Phone Number is required!")
    private String telephone;

    @NotBlank(message = "Specialty is required!")
    private String specialty;

}
