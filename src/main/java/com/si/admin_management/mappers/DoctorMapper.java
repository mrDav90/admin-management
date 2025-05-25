package com.si.admin_management.mappers;

import com.si.admin_management.dtos.doctors.DoctorDtoRequest;
import com.si.admin_management.dtos.doctors.DoctorDtoResponse;
import com.si.admin_management.entities.DoctorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface DoctorMapper {
    //@Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "specialty", target = "specialty")
    //@Mapping(source = "registrationNumber", target = "registrationNumber")
    DoctorEntity toDoctorEntity(DoctorDtoRequest doctorDtoRequest);
    DoctorDtoResponse toDoctorDtoResponse(DoctorEntity doctorEntity);
    List<DoctorDtoResponse> toDoctorDtoResponseList(List<DoctorEntity> doctorEntities);
}
