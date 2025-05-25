package com.si.admin_management.dtos.patients;

import com.si.admin_management.entities.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PatientDtoRequest {
    @NotBlank(message = "Firstname is required!")
    private String firstName;

    @NotBlank(message = "Lastname is required!")
    private String lastName;

    @NotNull(message = "Birthdate is required!")
    private Date birthDate;

    @NotBlank(message = "PlaceOfBirth is required!")
    private String placeOfBirth;

    @NotNull(message = "Gender is required!")
    private Gender gender;

    @NotBlank(message = "Telephone number is required!")
    private String phoneNumber;

    private String address;

    private String personToNotifyName;
    private String personToNotifyPhoneNumber;
}
