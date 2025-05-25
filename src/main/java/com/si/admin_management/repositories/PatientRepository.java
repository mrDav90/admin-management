package com.si.admin_management.repositories;

import com.si.admin_management.entities.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity,String> {
    Optional<PatientEntity> findByPhoneNumber(String phoneNumber);
}
