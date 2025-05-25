package com.si.admin_management.web.rest.controllers;

import com.si.admin_management.dtos.appointments.AppointmentDtoRequest;
import com.si.admin_management.dtos.appointments.AppointmentDtoResponse;
import com.si.admin_management.services.appointments.IAppointmentService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/appointments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Getter
@Setter
public class AppointmentController {
    private final IAppointmentService appointmentService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_appointment:read')")
    public ResponseEntity<Page<AppointmentDtoResponse>> getAppointments(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize ) {
        Page<AppointmentDtoResponse> appointments = appointmentService.getAppointments(pageNumber,pageSize);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_appointment:read')")
    public ResponseEntity<AppointmentDtoResponse> getAppointment(@PathVariable("id") String id){
        Optional<AppointmentDtoResponse> appointment = appointmentService.getAppointmentById(id);
        return new ResponseEntity<>(appointment.get(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_appointment:create')")
    public ResponseEntity<AppointmentDtoResponse> saveAppointment(@RequestBody @Valid AppointmentDtoRequest appointmentDtoRequest){
        Optional<AppointmentDtoResponse> appointmentDtoResponse = appointmentService.saveAppointment(appointmentDtoRequest);
        return appointmentDtoResponse.map(dtoResponse -> new ResponseEntity<>(dtoResponse, HttpStatus.CREATED)).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_appointment:update')")
    public ResponseEntity<AppointmentDtoResponse> updateAppointment(@PathVariable("id") String id, @RequestBody @Valid AppointmentDtoRequest appointmentDtoRequest){
        Optional<AppointmentDtoResponse> appointmentDtoResponse = appointmentService.updateAppointment(id, appointmentDtoRequest);
        return new ResponseEntity<>(appointmentDtoResponse.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_appointment:delete')")
    public ResponseEntity<Boolean> deleteAppointment(@PathVariable("id") String id){
        boolean result = appointmentService.deleteAppointment(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
