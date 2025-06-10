package com.si.admin_management.web.rest.controllers;

import com.si.admin_management.dtos.doctors.DoctorDtoRequest;
import com.si.admin_management.dtos.doctors.DoctorDtoResponse;
import com.si.admin_management.services.doctors.DoctorServiceImpl;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class DoctorControllerTest {
    @Mock
    private DoctorServiceImpl doctorService;
    @InjectMocks
    private DoctorController doctorController;

    private List<DoctorDtoResponse> doctorDtoResponseList;
    DoctorDtoRequest doctorDtoRequest = new DoctorDtoRequest();

    @BeforeEach
    void setUp() {
        doctorDtoResponseList = List.of(
                new DoctorDtoResponse("1", "Lucas", "Bernard", "lucas@gmail.com", "771232211", "Dentist","DOC-000001")
        );

        doctorDtoRequest.setFirstName("Lucas");
        doctorDtoRequest.setLastName("Bernard");
        doctorDtoRequest.setEmail("lucas@gmail.com");
        doctorDtoRequest.setTelephone("771232211");
        doctorDtoRequest.setSpecialty("Dentist");
    }

    @Test
    void testGetPaginatedDoctors_ReturnsOkResponse() {
        Page<DoctorDtoResponse> page = new PageImpl<>(doctorDtoResponseList);
        when(doctorService.getDoctors(0,1)).thenReturn(page);
        ResponseEntity<Page<DoctorDtoResponse>> response = doctorController.getDoctors(0,1);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1,response.getBody().getTotalElements());
        assertEquals(0 , response.getBody().getNumber());
        verify(doctorService, times(1)).getDoctors(0,1);
    }

    @Test
    void testGetPaginatedDoctors_ReturnsEmptyList() {
        Page<DoctorDtoResponse> page = new PageImpl<>(List.of());
        when(doctorService.getDoctors(0,1)).thenReturn(page);
        ResponseEntity<Page<DoctorDtoResponse>> response = doctorController.getDoctors(0,1);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(doctorService, times(1)).getDoctors(0,1);
    }

    @Test
    void testGetDoctor_ReturnsOkResponse() {
        when(doctorService.getDoctorById("1")).thenReturn(Optional.of(doctorDtoResponseList.get(0)));
        ResponseEntity<DoctorDtoResponse> response = doctorController.getDoctor("1");
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(doctorDtoResponseList.get(0),response.getBody());
        verify(doctorService, times(1)).getDoctorById("1");
    }

    @Test
    void testSaveDoctor_ReturnsOkResponse() {
        when(doctorService.saveDoctor(doctorDtoRequest)).thenReturn(Optional.of(doctorDtoResponseList.get(0)));
        ResponseEntity<DoctorDtoResponse> response = doctorController.saveDoctor(doctorDtoRequest);
        assertEquals(HttpStatus.CREATED , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(doctorDtoResponseList.get(0),response.getBody());
        verify(doctorService, times(1)).saveDoctor(doctorDtoRequest);
    }

    @Test
    void testUpdateDoctor_ReturnsOkResponse() {
        when(doctorService.updateDoctor("1", doctorDtoRequest)).thenReturn(Optional.of(doctorDtoResponseList.get(0)));
        ResponseEntity<DoctorDtoResponse> response = doctorController.updateDoctor("1", doctorDtoRequest);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(doctorDtoResponseList.get(0),response.getBody());
        verify(doctorService, times(1)).updateDoctor("1",doctorDtoRequest);
    }

    @Test
    void testDeleteDoctor_ReturnsOkResponse() {
        when(doctorService.deleteDoctor("1")).thenReturn(Boolean.TRUE);
        ResponseEntity<Boolean> response = doctorController.deleteDoctor("1");
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Boolean.TRUE,response.getBody());
        verify(doctorService, times(1)).deleteDoctor("1");
    }
}
