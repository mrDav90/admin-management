package com.si.admin_management.services.doctors;

import com.si.admin_management.dtos.doctors.DoctorDtoRequest;
import com.si.admin_management.dtos.doctors.DoctorDtoResponse;
import com.si.admin_management.entities.DoctorEntity;
import com.si.admin_management.exception.EntityExistsException;
import com.si.admin_management.exception.EntityNotFoundException;
import com.si.admin_management.mappers.DoctorMapper;
import com.si.admin_management.repositories.DoctorRepository;
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
public class DoctorServiceImpl implements IDoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final MessageSource messageSource;
    private final Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);
    private final RegistrationNumber registrationNumber = new RegistrationNumber();

    @Override
    public Optional<DoctorDtoResponse> saveDoctor(DoctorDtoRequest doctorDtoRequest){
        if (doctorRepository.findByEmail(doctorDtoRequest.getEmail()).isPresent()) {
            throw new EntityExistsException(messageSource.getMessage("doctor.exists", new Object[]{doctorDtoRequest.getEmail()}, Locale.getDefault()));
        }
        DoctorEntity doctor = doctorMapper.toDoctorEntity(doctorDtoRequest);
        doctor.setRegistrationNumber(registrationNumber.generate("DOC" , doctorRepository.count()));
        logger.info("Doctor : {}", doctor);

        DoctorEntity doctorEntity = doctorRepository.save(doctor);
        DoctorDtoResponse doctorDtoResponse = doctorMapper.toDoctorDtoResponse(doctorEntity);
        return Optional.of(doctorDtoResponse);
    }


    @Override
    public Page<DoctorDtoResponse> getDoctors(int pageNumber , int pageSize){
        Pageable pagedRequest = PageRequest.of(pageNumber,pageSize);
        Page<DoctorEntity> doctorsEntities = doctorRepository.findAll(pagedRequest);
        return doctorsEntities.map(doctorMapper::toDoctorDtoResponse);
    }

    @Override
    public Optional<DoctorDtoResponse> getDoctorById(String id){
        return doctorRepository.findById(id)
                .map(doctor -> Optional.of(doctorMapper.toDoctorDtoResponse(doctor)))
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("doctor.notfound", new Object[]{id}, Locale.getDefault())));
    }

    @Override
    public Optional<DoctorDtoResponse> updateDoctor(String id, DoctorDtoRequest doctorDtoRequest){
        return doctorRepository.findById(id)
                .map(doctor -> {
                    doctor.setFirstName(doctorDtoRequest.getFirstName());
                    doctor.setLastName(doctorDtoRequest.getLastName());
                    doctor.setEmail(doctorDtoRequest.getEmail());
                    doctor.setTelephone(doctorDtoRequest.getTelephone());
                    doctor.setSpecialty(doctorDtoRequest.getSpecialty());

                    var doctorEntity = doctorRepository.save(doctor);
                    return Optional.of(doctorMapper.toDoctorDtoResponse(doctorEntity));
                }).orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("doctor.notfound", new Object[]{id}, Locale.getDefault())));
    }

    @Override
    public boolean deleteDoctor(String id){
        var doctor = doctorRepository.findById(id);
        if (doctor.isEmpty()) {
            throw new EntityNotFoundException(messageSource.getMessage("doctor.notfound", new Object[]{id}, Locale.getDefault()));
        }
        doctorRepository.deleteById(id);
        return true;
    }
}
