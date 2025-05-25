package com.si.admin_management.services.appointments;

import com.si.admin_management.dtos.appointments.AppointmentDtoRequest;
import com.si.admin_management.dtos.appointments.AppointmentDtoResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IAppointmentService {
    Optional<AppointmentDtoResponse> saveAppointment(AppointmentDtoRequest appointmentDtoRequest);
    Page<AppointmentDtoResponse> getAppointments(int pageNumber , int pageSize);
    Optional<AppointmentDtoResponse> getAppointmentById(String appointmentId);
    Optional<AppointmentDtoResponse> updateAppointment(String appointmentId, AppointmentDtoRequest appointmentDtoRequest);
    boolean deleteAppointment(String appointmentId);
}
