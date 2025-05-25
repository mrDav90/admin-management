package com.si.admin_management.services.doctors;

import com.si.admin_management.dtos.doctors.DoctorDtoResponse;
import com.si.admin_management.dtos.doctors.DoctorDtoRequest;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IDoctorService {
    Optional<DoctorDtoResponse> saveDoctor(DoctorDtoRequest doctorDtoRequest);
    Page<DoctorDtoResponse> getDoctors(int pageNumber , int pageSize);
    Optional<DoctorDtoResponse> getDoctorById(String doctorId);
    Optional<DoctorDtoResponse> updateDoctor(String doctorId, DoctorDtoRequest doctorDtoRequest);
    boolean deleteDoctor(String doctorId);
}
