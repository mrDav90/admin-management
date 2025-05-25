package com.si.admin_management.services.patients;

import com.si.admin_management.dtos.patients.PatientDtoRequest;
import com.si.admin_management.dtos.patients.PatientDtoResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IPatientService {
    Optional<PatientDtoResponse> savePatient(PatientDtoRequest patientDtoRequest);
    Page<PatientDtoResponse> getPatients(int pageNumber , int pageSize);
    Optional<PatientDtoResponse> getPatientById(String patientId);
    Optional<PatientDtoResponse> updatePatient(String patientId, PatientDtoRequest patientDtoRequest);
    boolean deletePatient(String patientId);
}
