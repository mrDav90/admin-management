package com.si.admin_management.dtos.appointments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppointmentDtoRequest {
    @NotNull(message = "Appointment date is required!")
    private LocalDateTime appointmentDate;

    private String reason;

    @NotBlank(message = "Patient Id is required!")
    private String patientId;

    @NotBlank(message = "Doctor Id is required!")
    private String doctorId;
}
