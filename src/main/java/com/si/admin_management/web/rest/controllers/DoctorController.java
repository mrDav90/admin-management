package com.si.admin_management.web.rest.controllers;

import com.si.admin_management.dtos.doctors.DoctorDtoRequest;
import com.si.admin_management.dtos.doctors.DoctorDtoResponse;
import com.si.admin_management.services.doctors.IDoctorService;
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
@RequestMapping("/api/v1/doctors")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Getter
@Setter
public class DoctorController {
    private final IDoctorService doctorService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_staff:read')")
    public ResponseEntity<Page<DoctorDtoResponse>> getDoctors(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize ) {
        Page<DoctorDtoResponse> doctors = doctorService.getDoctors(pageNumber,pageSize);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_staff:read')")
    public ResponseEntity<DoctorDtoResponse> getDoctor(@PathVariable("id") String id){
        Optional<DoctorDtoResponse> doctor = doctorService.getDoctorById(id);
        return new ResponseEntity<>(doctor.get(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_staff:create')")
    public ResponseEntity<DoctorDtoResponse> saveDoctor(@RequestBody @Valid DoctorDtoRequest doctorDtoRequest){
        Optional<DoctorDtoResponse> doctorDtoResponse = doctorService.saveDoctor(doctorDtoRequest);
        return doctorDtoResponse.map(dtoResponse -> new ResponseEntity<>(dtoResponse, HttpStatus.CREATED)).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_staff:update')")
    public ResponseEntity<DoctorDtoResponse> updateDoctor(@PathVariable("id") String id, @RequestBody @Valid DoctorDtoRequest doctorDtoRequest){
        Optional<DoctorDtoResponse> doctorDtoResponse = doctorService.updateDoctor(id, doctorDtoRequest);
        return new ResponseEntity<>(doctorDtoResponse.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_staff:delete')")
    public ResponseEntity<Boolean> deleteDoctor(@PathVariable("id") String id){
        boolean result = doctorService.deleteDoctor(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
