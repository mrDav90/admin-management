package com.si.admin_management.dtos.patients;

import com.si.admin_management.entities.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PatientDtoResponse {
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String placeOfBirth;
    private Gender gender;
    private String phoneNumber;
    private String address;
    private String registrationNumber;
    private String personToNotifyName;
    private String personToNotifyPhoneNumber;
}
