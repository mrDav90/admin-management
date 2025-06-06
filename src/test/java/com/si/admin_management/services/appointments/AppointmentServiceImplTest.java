package com.si.admin_management.services.appointments;

import com.si.admin_management.dtos.appointments.AppointmentDtoRequest;
import com.si.admin_management.dtos.appointments.AppointmentDtoResponse;
import com.si.admin_management.dtos.doctors.DoctorDtoResponse;
import com.si.admin_management.dtos.patients.PatientDtoResponse;
import com.si.admin_management.entities.AppointmentEntity;
import com.si.admin_management.entities.Gender;
import com.si.admin_management.exception.EntityNotFoundException;
import com.si.admin_management.mappers.AppointmentMapper;
import com.si.admin_management.repositories.AppointmentRepository;
import com.si.admin_management.services.doctors.DoctorServiceImpl;
import com.si.admin_management.services.patients.PatientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AppointmentServiceImplTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @InjectMocks
    private AppointmentServiceImpl appointmentService;
    @Mock
    private DoctorServiceImpl doctorService;
    @Mock
    private PatientServiceImpl patientService;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private MessageSource messageSource;


    @Test
    void saveAppointmentOK() {
        when(patientService.getPatientById("1")).thenReturn(Optional.of(getPatientDtoResponse()));
        when(doctorService.getDoctorById("1")).thenReturn(Optional.of(getDoctorDtoResponse()));
        when(appointmentMapper.toAppointmentEntity(getAppointmentDtoRequest())).thenReturn(getAppointmentEntity());

        when(appointmentRepository.save(getAppointmentEntity())).thenReturn(getAppointmentEntity());
        when(appointmentMapper.toAppointmentDtoResponse(getAppointmentEntity())).thenReturn(getAppointmentDtoResponse());

        Optional<AppointmentDtoResponse> savedAppointment = appointmentService.saveAppointment(getAppointmentDtoRequest());
        assertTrue(savedAppointment.isPresent());
        assertEquals("1", savedAppointment.get().getId());
    }


    @Test
    void getAppointmentByIdOK() {
        when(appointmentRepository.findById(anyString())).thenReturn(Optional.of(getAppointmentEntity()));
        when(appointmentMapper.toAppointmentDtoResponse(any())).thenReturn(getAppointmentDtoResponse());

        Optional<AppointmentDtoResponse> appointment = appointmentService.getAppointmentById("1");
        assertTrue(appointment.isPresent());
        assertEquals("1", appointment.get().getId());
    }

    @Test
    void getAppointmentsOK() {
        PageRequest pageable = PageRequest.of(0, 1);
        Page<AppointmentEntity> pageAppointmentEntity = new PageImpl<>(List.of(getAppointmentEntity()));
        when(appointmentRepository.findAll(pageable)).thenReturn(pageAppointmentEntity);
        when(appointmentMapper.toAppointmentDtoResponse(any())).thenReturn(getAppointmentDtoResponse());

        Page<AppointmentDtoResponse> appointments = appointmentService.getAppointments(0, 1);
        assertEquals(1, appointments.getContent().size());
        assertEquals(getAppointmentDtoResponse().getId(), appointments.getContent().get(0).getId());
    }

    @Test
    void getAppointmentByIdKO() {
        when(appointmentRepository.findById(anyString())).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("appointment.notfound"), any(), any(Locale.class))).thenReturn("Appointment with id=1 is not found");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> appointmentService.getAppointmentById("1"));
        assertEquals("Appointment with id=1 is not found", exception.getMessage());
    }

    @Test
    void updateAppointmentOK() {
        when(appointmentRepository.findById(anyString())).thenReturn(Optional.of(getAppointmentEntity()));
        when(appointmentRepository.save(any())).thenReturn(getAppointmentEntity());
        when(appointmentMapper.toAppointmentDtoResponse(any())).thenReturn(getAppointmentDtoResponse());

        Optional<AppointmentDtoResponse> updatedAppointment = appointmentService.updateAppointment("1", getAppointmentDtoRequest());
        assertTrue(updatedAppointment.isPresent());
        assertEquals("1", updatedAppointment.get().getId());
    }

    @Test
    void updateAppointmentKO() {
        when(appointmentRepository.findById(anyString())).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("appointment.notfound"), any(), any(Locale.class)))
                .thenReturn("Appointment not found");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> appointmentService.updateAppointment("1" ,getAppointmentDtoRequest()));
        assertEquals("Appointment not found", exception.getMessage());
    }

    @Test
    void deleteAppointmentOK() {
        when(appointmentRepository.findById(anyString())).thenReturn(Optional.of(getAppointmentEntity()));
        boolean result = appointmentService.deleteAppointment(anyString());
        assertTrue(result);
        verify(appointmentRepository, times(1)).deleteById(anyString());
    }

    @Test
    void deleteAppointmentKO() {
        when(appointmentRepository.findById(anyString())).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("appointment.notfound"), any(), any(Locale.class)))
                .thenReturn("Appointment not found");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> appointmentService.deleteAppointment("1"));
        assertEquals("Appointment not found", exception.getMessage());
    }

    private AppointmentDtoRequest getAppointmentDtoRequest(){
        AppointmentDtoRequest appointmentDtoRequest = new AppointmentDtoRequest();
        appointmentDtoRequest.setAppointmentDate(LocalDateTime.now());
        appointmentDtoRequest.setReason("Consultation");
        appointmentDtoRequest.setPatientId("1");
        appointmentDtoRequest.setDoctorId("1");
        return appointmentDtoRequest;
    }
    private AppointmentEntity getAppointmentEntity(){
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setId("1");
        appointmentEntity.setAppointmentDate(LocalDateTime.now());
        appointmentEntity.setReason("Consultation");
        appointmentEntity.setPatientId("1");
        appointmentEntity.setDoctorId("1");
        appointmentEntity.setAppointmentNum("APT-000001");
        appointmentEntity.setCreatedBy("david");
        appointmentEntity.setCreatedDate(LocalDateTime.now());
        appointmentEntity.setUpdatedDate(LocalDateTime.now());
        return appointmentEntity;
    }
    private AppointmentDtoResponse getAppointmentDtoResponse(){
        AppointmentDtoResponse appointmentDtoResponse = new AppointmentDtoResponse();
        appointmentDtoResponse.setId("1");
        appointmentDtoResponse.setAppointmentDate(LocalDateTime.now());
        appointmentDtoResponse.setReason("Consultation");
        appointmentDtoResponse.setPatientId("1");
        appointmentDtoResponse.setPatientName("Lucas Bernard");
        appointmentDtoResponse.setDoctorId("1");
        appointmentDtoResponse.setDoctorId("1");
        appointmentDtoResponse.setDoctorName("Lucas Bernard");
        appointmentDtoResponse.setAppointmentNum("APT-000001");
        return appointmentDtoResponse;
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
