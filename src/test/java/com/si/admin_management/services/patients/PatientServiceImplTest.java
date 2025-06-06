package com.si.admin_management.services.patients;

import com.si.admin_management.dtos.patients.PatientDtoRequest;
import com.si.admin_management.dtos.patients.PatientDtoResponse;
import com.si.admin_management.entities.Gender;
import com.si.admin_management.entities.PatientEntity;
import com.si.admin_management.exception.EntityExistsException;
import com.si.admin_management.exception.EntityNotFoundException;
import com.si.admin_management.mappers.PatientMapper;
import com.si.admin_management.repositories.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {
    @Mock
    private PatientRepository patientRepository;
    @InjectMocks
    private PatientServiceImpl patientService;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private MessageSource messageSource;

    @Test
    void savePatientOK() {
        when(patientRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        when(patientMapper.toPatientEntity(any())).thenReturn(getPatientEntity());
        when(patientRepository.save(any())).thenReturn(getPatientEntity());
        when(patientMapper.toPatientDtoResponse(any())).thenReturn(getPatientDtoResponse());

        Optional<PatientDtoResponse> savedPatient = patientService.savePatient(getPatientDtoRequest());
        assertTrue(savedPatient.isPresent());
        assertEquals("1", savedPatient.get().getId());
    }

    @Test
    void savePatientKO() {
        when(patientRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(getPatientEntity()));
        when(messageSource.getMessage(eq("patient.exists"), any(), any(Locale.class)))
                .thenReturn("Patient with phone number already exists.");

        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> patientService.savePatient(getPatientDtoRequest()));
        assertEquals("Patient with phone number already exists.", exception.getMessage());
    }

    @Test
    void getPatientByIdOK() {
        when(patientRepository.findById(anyString())).thenReturn(Optional.of(getPatientEntity()));
        when(patientMapper.toPatientDtoResponse(any())).thenReturn(getPatientDtoResponse());

        Optional<PatientDtoResponse> patient = patientService.getPatientById("1");
        assertTrue(patient.isPresent());
        assertEquals("1", patient.get().getId());
    }

    @Test
    void getPatientsOK() {
        PageRequest pageable = PageRequest.of(0, 1);
        Page<PatientEntity> pagePatientEntity = new PageImpl<>(List.of(getPatientEntity()));
        when(patientRepository.findAll(pageable)).thenReturn(pagePatientEntity);
        when(patientMapper.toPatientDtoResponse(any())).thenReturn(getPatientDtoResponse());

        Page<PatientDtoResponse> patients = patientService.getPatients(0, 1);
        assertEquals(1, patients.getContent().size());
        assertEquals(getPatientDtoResponse().getId(), patients.getContent().get(0).getId());
    }

    @Test
    void getPatientByIdKO() {
        when(patientRepository.findById(anyString())).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("patient.notfound"), any(), any(Locale.class))).thenReturn("Patient with id=1 is not found");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> patientService.getPatientById("1"));
        assertEquals("Patient with id=1 is not found", exception.getMessage());
    }

    @Test
    void updatePatientOK() {
        when(patientRepository.findById(anyString())).thenReturn(Optional.of(getPatientEntity()));
        when(patientRepository.save(any())).thenReturn(getPatientEntity());
        when(patientMapper.toPatientDtoResponse(any())).thenReturn(getPatientDtoResponse());

        Optional<PatientDtoResponse> updatedPatient = patientService.updatePatient("1", getPatientDtoRequest());
        assertTrue(updatedPatient.isPresent());
        assertEquals("1", updatedPatient.get().getId());
    }

    @Test
    void updatePatientKO() {
        when(patientRepository.findById(anyString())).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("patient.notfound"), any(), any(Locale.class)))
                .thenReturn("Patient not found");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> patientService.updatePatient("1" ,getPatientDtoRequest()));
        assertEquals("Patient not found", exception.getMessage());
    }

    @Test
    void deletePatientOK() {
        when(patientRepository.findById(anyString())).thenReturn(Optional.of(getPatientEntity()));
        boolean result = patientService.deletePatient(anyString());
        assertTrue(result);
        verify(patientRepository, times(1)).deleteById(anyString());
    }

    @Test
    void deletePatientKO() {
        when(patientRepository.findById(anyString())).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("patient.notfound"), any(), any(Locale.class)))
                .thenReturn("Patient not found");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> patientService.deletePatient("1"));
        assertEquals("Patient not found", exception.getMessage());
    }

    private PatientDtoRequest getPatientDtoRequest(){
        PatientDtoRequest patientDtoRequest = new PatientDtoRequest();
        patientDtoRequest.setFirstName("Lucas");
        patientDtoRequest.setLastName("Bernard");
        patientDtoRequest.setBirthDate(LocalDate.of(1985,7,22));
        patientDtoRequest.setPlaceOfBirth("Paris");
        patientDtoRequest.setGender(Gender.MALE);
        patientDtoRequest.setPhoneNumber("771232211");
        patientDtoRequest.setAddress("15 Rue de la Paix, 75002 Paris");
        patientDtoRequest.setPersonToNotifyName("Marie Dupont");
        patientDtoRequest.setPersonToNotifyPhoneNumber("0798765432");
        return patientDtoRequest;
    }
    private PatientEntity getPatientEntity(){
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setId("1");
        patientEntity.setFirstName("Lucas");
        patientEntity.setLastName("Bernard");
        patientEntity.setBirthDate(LocalDate.of(1985,7,22));
        patientEntity.setPlaceOfBirth("Paris");
        patientEntity.setGender(Gender.MALE);
        patientEntity.setPhoneNumber("771232211");
        patientEntity.setAddress("15 Rue de la Paix, 75002 Paris");
        patientEntity.setPersonToNotifyName("Marie Dupont");
        patientEntity.setPersonToNotifyPhoneNumber("0798765432");
        patientEntity.setRegistrationNumber("PAT-000001");
        return patientEntity;
    }
    private PatientDtoResponse getPatientDtoResponse(){
        PatientDtoResponse patientDtoResponse = new PatientDtoResponse();
        patientDtoResponse.setId("1");
        patientDtoResponse.setFirstName("Lucas");
        patientDtoResponse.setLastName("Bernard");
        patientDtoResponse.setBirthDate(LocalDate.of(1985,7,22));
        patientDtoResponse.setPlaceOfBirth("Paris");
        patientDtoResponse.setGender(Gender.MALE);
        patientDtoResponse.setPhoneNumber("771232211");
        patientDtoResponse.setAddress("15 Rue de la Paix, 75002 Paris");
        patientDtoResponse.setPersonToNotifyName("Marie Dupont");
        patientDtoResponse.setPersonToNotifyPhoneNumber("0798765432");
        patientDtoResponse.setRegistrationNumber("PAT-000001");
        return patientDtoResponse;
    }

}
