package com.si.admin_management.services.doctors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.si.admin_management.dtos.doctors.DoctorDtoRequest;
import com.si.admin_management.dtos.doctors.DoctorDtoResponse;
import com.si.admin_management.entities.DoctorEntity;
import com.si.admin_management.exception.EntityExistsException;
import com.si.admin_management.exception.EntityNotFoundException;
import com.si.admin_management.mappers.DoctorMapper;
import com.si.admin_management.repositories.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class DoctorServiceImplTest {
    @Mock
    private DoctorRepository doctorRepository;
    @InjectMocks
    private DoctorServiceImpl doctorService;
    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private MessageSource messageSource;


    @Test
    void saveDoctorOK() {
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(doctorMapper.toDoctorEntity(any())).thenReturn(getDoctorEntity());
        when(doctorRepository.save(any())).thenReturn(getDoctorEntity());
        when(doctorMapper.toDoctorDtoResponse(any())).thenReturn(getDoctorDtoResponse());

        Optional<DoctorDtoResponse> savedDoctor = doctorService.saveDoctor(getDoctorDtoRequest());
        assertTrue(savedDoctor.isPresent());
        assertEquals("1", savedDoctor.get().getId());
    }

    @Test
    void saveDoctorKO() {
        when(doctorRepository.findByEmail(anyString())).thenReturn(Optional.of(getDoctorEntity()));
        when(messageSource.getMessage(eq("doctor.exists"), any(), any(Locale.class)))
                .thenReturn("Doctor with this email is already exists.");

        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> doctorService.saveDoctor(getDoctorDtoRequest()));
        assertEquals("Doctor with this email is already exists.", exception.getMessage());
    }

    @Test
    void getDoctorByIdOK() {
        when(doctorRepository.findById(anyString())).thenReturn(Optional.of(getDoctorEntity()));
        when(doctorMapper.toDoctorDtoResponse(any())).thenReturn(getDoctorDtoResponse());

        Optional<DoctorDtoResponse> doctor = doctorService.getDoctorById("1");
        assertTrue(doctor.isPresent());
        assertEquals("1", doctor.get().getId());
    }

    @Test
    void getDoctorsOK() {
        PageRequest pageable = PageRequest.of(0, 1);
        Page<DoctorEntity> pageDoctorEntity = new PageImpl<>(List.of(getDoctorEntity()));
        when(doctorRepository.findAll(pageable)).thenReturn(pageDoctorEntity);
        when(doctorMapper.toDoctorDtoResponse(any())).thenReturn(getDoctorDtoResponse());

        Page<DoctorDtoResponse> doctors = doctorService.getDoctors(0, 1);
        assertEquals(1, doctors.getContent().size());
        assertEquals(getDoctorDtoResponse().getId(), doctors.getContent().get(0).getId());
    }

    @Test
    void getDoctorByIdKO() {
        when(doctorRepository.findById(anyString())).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("doctor.notfound"), any(), any(Locale.class))).thenReturn("Doctor with id=1 is not found");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> doctorService.getDoctorById("1"));
        assertEquals("Doctor with id=1 is not found", exception.getMessage());
    }

    @Test
    void updateDoctorOK() {
        when(doctorRepository.findById(anyString())).thenReturn(Optional.of(getDoctorEntity()));
        when(doctorRepository.save(any())).thenReturn(getDoctorEntity());
        when(doctorMapper.toDoctorDtoResponse(any())).thenReturn(getDoctorDtoResponse());

        Optional<DoctorDtoResponse> updatedDoctor = doctorService.updateDoctor("1", getDoctorDtoRequest());
        assertTrue(updatedDoctor.isPresent());
        assertEquals("1", updatedDoctor.get().getId());
    }

    @Test
    void updateDoctorKO() {
        when(doctorRepository.findById(anyString())).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("doctor.notfound"), any(), any(Locale.class)))
                .thenReturn("Doctor not found");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> doctorService.updateDoctor("1" ,getDoctorDtoRequest()));
        assertEquals("Doctor not found", exception.getMessage());
    }

    @Test
    void deleteDoctorOK() {
        when(doctorRepository.findById(anyString())).thenReturn(Optional.of(getDoctorEntity()));
        boolean result = doctorService.deleteDoctor(anyString());
        assertTrue(result);
        verify(doctorRepository, times(1)).deleteById(anyString());
    }

    @Test
    void deleteDoctorKO() {
        when(doctorRepository.findById(anyString())).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("doctor.notfound"), any(), any(Locale.class)))
                .thenReturn("Doctor not found");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> doctorService.deleteDoctor("1"));
        assertEquals("Doctor not found", exception.getMessage());
    }

    private DoctorDtoRequest getDoctorDtoRequest(){
        DoctorDtoRequest doctorDtoRequest = new DoctorDtoRequest();
        doctorDtoRequest.setFirstName("Lucas");
        doctorDtoRequest.setLastName("Bernard");
        doctorDtoRequest.setEmail("lucas@gmail.com");
        doctorDtoRequest.setTelephone("771232211");
        doctorDtoRequest.setSpecialty("Dentist");
        return doctorDtoRequest;
    }

    private DoctorEntity getDoctorEntity(){
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setId("1");
        doctorEntity.setFirstName("Lucas");
        doctorEntity.setLastName("Bernard");
        doctorEntity.setEmail("lucas@gmail.com");
        doctorEntity.setTelephone("771232211");
        doctorEntity.setSpecialty("Dentist");
        doctorEntity.setRegistrationNumber("DOC-000001");
        return doctorEntity;
    }
    private DoctorDtoResponse getDoctorDtoResponse(){
        DoctorDtoResponse doctorDtoResponse = new DoctorDtoResponse();
        doctorDtoResponse.setId("1");
        doctorDtoResponse.setFirstName("Lucas");
        doctorDtoResponse.setLastName("Bernard");
        doctorDtoResponse.setEmail("lucas@gmail.com");
        doctorDtoResponse.setTelephone("771232211");
        doctorDtoResponse.setSpecialty("Dentist");
        doctorDtoResponse.setRegistrationNumber("DOC-000001");
        return doctorDtoResponse;
    }
}
