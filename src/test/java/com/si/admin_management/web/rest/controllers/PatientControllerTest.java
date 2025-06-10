package com.si.admin_management.web.rest.controllers;

import com.si.admin_management.dtos.patients.PatientDtoRequest;
import com.si.admin_management.dtos.patients.PatientDtoResponse;
import com.si.admin_management.entities.Gender;
import com.si.admin_management.services.patients.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientControllerTest {

    @Mock
    private PatientServiceImpl patientService;
    @InjectMocks
    private PatientController patientController;

    private List<PatientDtoResponse> patientDtoResponseList;
    PatientDtoRequest patientDtoRequest = new PatientDtoRequest();

    @BeforeEach
    void setUp() {
        patientDtoResponseList = List.of(
                new PatientDtoResponse("1", "Lucas", "Bernard",LocalDate.of(1985,7,22), "Paris", Gender.MALE, "771232211", "15 Rue de la Paix, 75002 Paris","PAT-000001","Marie Dupont","0798765432" )
        );

        patientDtoRequest.setFirstName("Lucas");
        patientDtoRequest.setLastName("Bernard");
        patientDtoRequest.setBirthDate(LocalDate.of(1985,7,22));
        patientDtoRequest.setPlaceOfBirth("Paris");
        patientDtoRequest.setGender(Gender.MALE);
        patientDtoRequest.setPhoneNumber("771232211");
        patientDtoRequest.setAddress("15 Rue de la Paix, 75002 Paris");
        patientDtoRequest.setPersonToNotifyName("Marie Dupont");
        patientDtoRequest.setPersonToNotifyPhoneNumber("0798765432");
    }

    @Test
    void testGetPaginatedPatients_ReturnsOkResponse() {
        Page<PatientDtoResponse> page = new PageImpl<>(patientDtoResponseList);
        when(patientService.getPatients(0,1)).thenReturn(page);
        ResponseEntity<Page<PatientDtoResponse>> response = patientController.getPatients(0,1);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1,response.getBody().getTotalElements());
        assertEquals(0 , response.getBody().getNumber());
        verify(patientService, times(1)).getPatients(0,1);
    }

    @Test
    void testGetPaginatedPatients_ReturnsEmptyList() {
        Page<PatientDtoResponse> page = new PageImpl<>(List.of());
        when(patientService.getPatients(0,1)).thenReturn(page);
        ResponseEntity<Page<PatientDtoResponse>> response = patientController.getPatients(0,1);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(patientService, times(1)).getPatients(0,1);
    }

    @Test
    void testGetPatient_ReturnsOkResponse() {
        when(patientService.getPatientById("1")).thenReturn(Optional.of(patientDtoResponseList.get(0)));
        ResponseEntity<PatientDtoResponse> response = patientController.getPatient("1");
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(patientDtoResponseList.get(0),response.getBody());
        verify(patientService, times(1)).getPatientById("1");
    }

    @Test
    void testSavePatient_ReturnsOkResponse() {
        when(patientService.savePatient(patientDtoRequest)).thenReturn(Optional.of(patientDtoResponseList.get(0)));
        ResponseEntity<PatientDtoResponse> response = patientController.savePatient(patientDtoRequest);
        assertEquals(HttpStatus.CREATED , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(patientDtoResponseList.get(0),response.getBody());
        verify(patientService, times(1)).savePatient(patientDtoRequest);
    }

    @Test
    void testUpdatePatient_ReturnsOkResponse() {
        when(patientService.updatePatient("1", patientDtoRequest)).thenReturn(Optional.of(patientDtoResponseList.get(0)));
        ResponseEntity<PatientDtoResponse> response = patientController.updatePatient("1", patientDtoRequest);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(patientDtoResponseList.get(0),response.getBody());
        verify(patientService, times(1)).updatePatient("1",patientDtoRequest);
    }

    @Test
    void testDeletePatient_ReturnsOkResponse() {
        when(patientService.deletePatient("1")).thenReturn(Boolean.TRUE);
        ResponseEntity<Boolean> response = patientController.deletePatient("1");
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Boolean.TRUE,response.getBody());
        verify(patientService, times(1)).deletePatient("1");
    }

}
