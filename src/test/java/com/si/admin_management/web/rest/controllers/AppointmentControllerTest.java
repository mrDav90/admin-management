package com.si.admin_management.web.rest.controllers;

import com.si.admin_management.dtos.appointments.AppointmentDtoRequest;
import com.si.admin_management.dtos.appointments.AppointmentDtoResponse;
import com.si.admin_management.entities.AppointmentStatus;
import com.si.admin_management.services.appointments.AppointmentServiceImpl;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class AppointmentControllerTest {
    @Mock
    private AppointmentServiceImpl appointmentService;
    @InjectMocks
    private AppointmentController appointmentController;

    private List<AppointmentDtoResponse> appointmentDtoResponseList;
    AppointmentDtoRequest appointmentDtoRequest = new AppointmentDtoRequest();

    @BeforeEach
    void setUp() {
        appointmentDtoResponseList = List.of(
                new AppointmentDtoResponse(
                        "1",
                        LocalDateTime.of(2025, 6, 15, 10,30,0) ,
                        "Consultation générale",
                        AppointmentStatus.SCHEDULED,
                        "1",
                        "1",
                        "Lucas",
                        "Bernard",
                        "RDV-000001"
                )
        );

        appointmentDtoRequest.setAppointmentDate(LocalDateTime.of(2025, 6, 15, 10,30,0));
        appointmentDtoRequest.setReason("Consultation générale");
        appointmentDtoRequest.setDoctorId("1");
        appointmentDtoRequest.setPatientId("1");
    }

    @Test
    void testGetPaginatedAppointments_ReturnsOkResponse() {
        Page<AppointmentDtoResponse> page = new PageImpl<>(appointmentDtoResponseList);
        when(appointmentService.getAppointments(0,1)).thenReturn(page);
        ResponseEntity<Page<AppointmentDtoResponse>> response = appointmentController.getAppointments(0,1);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1,response.getBody().getTotalElements());
        assertEquals(0 , response.getBody().getNumber());
        verify(appointmentService, times(1)).getAppointments(0,1);
    }

    @Test
    void testGetPaginatedAppointments_ReturnsEmptyList() {
        Page<AppointmentDtoResponse> page = new PageImpl<>(List.of());
        when(appointmentService.getAppointments(0,1)).thenReturn(page);
        ResponseEntity<Page<AppointmentDtoResponse>> response = appointmentController.getAppointments(0,1);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(appointmentService, times(1)).getAppointments(0,1);
    }

    @Test
    void testGetAppointment_ReturnsOkResponse() {
        when(appointmentService.getAppointmentById("1")).thenReturn(Optional.of(appointmentDtoResponseList.get(0)));
        ResponseEntity<AppointmentDtoResponse> response = appointmentController.getAppointment("1");
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(appointmentDtoResponseList.get(0),response.getBody());
        verify(appointmentService, times(1)).getAppointmentById("1");
    }

    @Test
    void testSaveAppointment_ReturnsOkResponse() {
        when(appointmentService.saveAppointment(appointmentDtoRequest)).thenReturn(Optional.of(appointmentDtoResponseList.get(0)));
        ResponseEntity<AppointmentDtoResponse> response = appointmentController.saveAppointment(appointmentDtoRequest);
        assertEquals(HttpStatus.CREATED , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(appointmentDtoResponseList.get(0),response.getBody());
        verify(appointmentService, times(1)).saveAppointment(appointmentDtoRequest);
    }

    @Test
    void testUpdateAppointment_ReturnsOkResponse() {
        when(appointmentService.updateAppointment("1", appointmentDtoRequest)).thenReturn(Optional.of(appointmentDtoResponseList.get(0)));
        ResponseEntity<AppointmentDtoResponse> response = appointmentController.updateAppointment("1", appointmentDtoRequest);
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(appointmentDtoResponseList.get(0),response.getBody());
        verify(appointmentService, times(1)).updateAppointment("1",appointmentDtoRequest);
    }

    @Test
    void testDeleteAppointment_ReturnsOkResponse() {
        when(appointmentService.deleteAppointment("1")).thenReturn(Boolean.TRUE);
        ResponseEntity<Boolean> response = appointmentController.deleteAppointment("1");
        assertEquals(HttpStatus.OK , response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Boolean.TRUE,response.getBody());
        verify(appointmentService, times(1)).deleteAppointment("1");
    }
}
