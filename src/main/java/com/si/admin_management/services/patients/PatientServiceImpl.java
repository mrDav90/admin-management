package com.si.admin_management.services.patients;

import com.si.admin_management.dtos.patients.PatientDtoRequest;
import com.si.admin_management.dtos.patients.PatientDtoResponse;
import com.si.admin_management.entities.PatientEntity;
import com.si.admin_management.exception.EntityExistsException;
import com.si.admin_management.exception.EntityNotFoundException;
import com.si.admin_management.mappers.PatientMapper;
import com.si.admin_management.repositories.PatientRepository;
import com.si.admin_management.utils.RegistrationNumber;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements IPatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final MessageSource messageSource;
    private final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);
    private final RegistrationNumber registrationNumber = new RegistrationNumber();

    @Override
    public Optional<PatientDtoResponse> savePatient(PatientDtoRequest patientDtoRequest){
        if (patientRepository.findByPhoneNumber(patientDtoRequest.getPhoneNumber()).isPresent()) {
            throw new EntityExistsException(messageSource.getMessage("patient.exists", new Object[]{patientDtoRequest.getPhoneNumber()}, Locale.getDefault()));
        }
        PatientEntity patient = patientMapper.toPatientEntity(patientDtoRequest);
        patient.setRegistrationNumber(registrationNumber.generate("PAT" , patientRepository.count()));
        logger.info("Patient : {}", patient);

        PatientEntity patientEntity = patientRepository.save(patient);
        PatientDtoResponse patientDtoResponse = patientMapper.toPatientDtoResponse(patientEntity);
        return Optional.of(patientDtoResponse);
    }


    @Override
    public Page<PatientDtoResponse> getPatients(int pageNumber , int pageSize){
        Pageable pagedRequest = PageRequest.of(pageNumber,pageSize);
        Page<PatientEntity> patientsEntities = patientRepository.findAll(pagedRequest);
        return patientsEntities.map(patientMapper::toPatientDtoResponse);
    }

    @Override
    public Optional<PatientDtoResponse> getPatientById(String id){
        return patientRepository.findById(id)
                .map(patient -> Optional.of(patientMapper.toPatientDtoResponse(patient)))
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("patient.notfound", new Object[]{id}, Locale.getDefault())));
    }

    @Override
    public Optional<PatientDtoResponse> updatePatient(String id, PatientDtoRequest patientDtoRequest){
        return patientRepository.findById(id)
                .map(patient -> {
                    patient.setFirstName(patientDtoRequest.getFirstName());
                    patient.setLastName(patientDtoRequest.getLastName());
                    patient.setBirthDate(patientDtoRequest.getBirthDate());
                    patient.setPlaceOfBirth(patientDtoRequest.getPlaceOfBirth());
                    patient.setPhoneNumber(patientDtoRequest.getPhoneNumber());
                    patient.setAddress(patientDtoRequest.getAddress());
                    patient.setPersonToNotifyName(patientDtoRequest.getPersonToNotifyName());
                    patient.setPersonToNotifyPhoneNumber(patientDtoRequest.getPersonToNotifyPhoneNumber());
                    patient.setGender(patientDtoRequest.getGender());

                    var patientEntity = patientRepository.save(patient);
                    return Optional.of(patientMapper.toPatientDtoResponse(patientEntity));
                }).orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("patient.notfound", new Object[]{id}, Locale.getDefault())));
    }

    @Override
    public boolean deletePatient(String id){
        var patient = patientRepository.findById(id);
        if (patient.isEmpty()) {
            throw new EntityNotFoundException(messageSource.getMessage("patient.notfound", new Object[]{id}, Locale.getDefault()));
        }
        patientRepository.deleteById(id);
        return true;
    }
}
