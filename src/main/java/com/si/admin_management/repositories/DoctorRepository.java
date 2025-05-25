package com.si.admin_management.repositories;

import com.si.admin_management.entities.DoctorEntity;
import com.si.admin_management.entities.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, String> {
    Optional<DoctorEntity> findByEmail(String email);
    Optional<PatientEntity> findByTelephone(String telephone);
}
