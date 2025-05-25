package com.si.admin_management.dtos.appointments;

import com.si.admin_management.entities.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppointmentDtoResponse {
    private String id;
    private LocalDateTime appointmentDate;
    private String reason;
    private AppointmentStatus status;
    private String patientId;
    private String doctorId;
    private String doctorName;
    private String patientName;
    private String appointmentNum;
}
