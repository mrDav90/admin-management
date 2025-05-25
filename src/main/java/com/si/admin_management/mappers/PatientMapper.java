package com.si.admin_management.mappers;

import com.si.admin_management.dtos.patients.PatientDtoRequest;
import com.si.admin_management.dtos.patients.PatientDtoResponse;
import com.si.admin_management.entities.PatientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface PatientMapper {
    //@Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "birthDate", target = "birthDate")
    @Mapping(source = "placeOfBirth", target = "placeOfBirth")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "address", target = "address")
    //@Mapping(source = "registrationNumber", target = "registrationNumber")
    @Mapping(source = "personToNotifyName", target = "personToNotifyName")
    @Mapping(source = "personToNotifyPhoneNumber", target = "personToNotifyPhoneNumber")

    PatientEntity toPatientEntity(PatientDtoRequest patientDtoRequest);
    PatientDtoResponse toPatientDtoResponse(PatientEntity patientEntity);
    List<PatientDtoResponse> toPatientDtoResponseList(List<PatientEntity> patientEntities);
}
