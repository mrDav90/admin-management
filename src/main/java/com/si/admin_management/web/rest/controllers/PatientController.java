package com.si.admin_management.web.rest.controllers;

import com.si.admin_management.dtos.patients.PatientDtoRequest;
import com.si.admin_management.dtos.patients.PatientDtoResponse;
import com.si.admin_management.services.patients.IPatientService;
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
@RequestMapping("/api/v1/patients")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Getter
@Setter
public class PatientController {
    private final IPatientService patientService;

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_patient:read')")
    public ResponseEntity<Page<PatientDtoResponse>> getPatients(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize ) {
        Page<PatientDtoResponse> patients = patientService.getPatients(pageNumber,pageSize);
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_patient:read')")
    public ResponseEntity<PatientDtoResponse> getPatient(@PathVariable("id") String id){
        Optional<PatientDtoResponse> patient = patientService.getPatientById(id);
        return new ResponseEntity<>(patient.get(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_patient:create')")
    public ResponseEntity<PatientDtoResponse> savePatient(@RequestBody @Valid PatientDtoRequest patientDtoRequest){
        Optional<PatientDtoResponse> patientDtoResponse = patientService.savePatient(patientDtoRequest);
        return patientDtoResponse.map(dtoResponse -> new ResponseEntity<>(dtoResponse, HttpStatus.CREATED)).orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_patient:update')")
    public ResponseEntity<PatientDtoResponse> updatePatient(@PathVariable("id") String id, @RequestBody @Valid PatientDtoRequest patientDtoRequest){
        Optional<PatientDtoResponse> patientDtoResponse = patientService.updatePatient(id, patientDtoRequest);
        return new ResponseEntity<>(patientDtoResponse.get(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_patient:delete')")
    public ResponseEntity<Boolean> deletePatient(@PathVariable("id") String id){
        boolean result = patientService.deletePatient(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
