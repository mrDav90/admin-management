package com.si.admin_management.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "appointments")
public class AppointmentEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private LocalDateTime appointmentDate;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column(nullable = false)
    private String patientId;

    @Column(nullable = false)
    private String doctorId;

    @Column(nullable = false)
    private String appointmentNum;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @Column(nullable = false)
    private String createdBy;
}
