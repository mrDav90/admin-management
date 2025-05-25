package com.si.admin_management.mappers;

import com.si.admin_management.dtos.appointments.AppointmentDtoRequest;
import com.si.admin_management.dtos.appointments.AppointmentDtoResponse;
import com.si.admin_management.entities.AppointmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AppointmentMapper {
    //@Mapping(source = "id", target = "id")
    @Mapping(source = "appointmentDate", target = "appointmentDate")
    @Mapping(source = "reason", target = "reason")
    //@Mapping(source = "status", target = "status")
    @Mapping(source = "patientId", target = "patientId")
    @Mapping(source = "doctorId", target = "doctorId")
    //@Mapping(source = "doctorName", target = "doctorName")
    //@Mapping(source = "patientName", target = "patientName")
    //@Mapping(source = "appointmentNum", target = "appointmentNum")
    AppointmentEntity toAppointmentEntity(AppointmentDtoRequest appointmentDtoRequest);
    AppointmentDtoResponse toAppointmentDtoResponse(AppointmentEntity appointmentEntity);
    List<AppointmentDtoResponse> toAppointmentDtoResponseList(List<AppointmentEntity> appointmentEntities);
}
