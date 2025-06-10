package com.si.admin_management.services.appointments;

import com.si.admin_management.dtos.appointments.AppointmentDtoRequest;
import com.si.admin_management.dtos.appointments.AppointmentDtoResponse;
import com.si.admin_management.dtos.doctors.DoctorDtoResponse;
import com.si.admin_management.dtos.patients.PatientDtoResponse;
import com.si.admin_management.entities.AppointmentEntity;
import com.si.admin_management.entities.DoctorEntity;
import com.si.admin_management.entities.Gender;
import com.si.admin_management.entities.PatientEntity;
import com.si.admin_management.exception.EntityNotFoundException;
import com.si.admin_management.mappers.AppointmentMapper;
import com.si.admin_management.repositories.AppointmentRepository;
import com.si.admin_management.services.doctors.DoctorServiceImpl;
import com.si.admin_management.services.patients.PatientServiceImpl;
import com.si.admin_management.utils.RegistrationNumber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @Mock
    private RegistrationNumber registrationNumber = new RegistrationNumber();

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;

    private AppointmentDtoRequest appointmentDtoRequest = new AppointmentDtoRequest();
    private AppointmentEntity appointmentEntity = new AppointmentEntity();
    private AppointmentDtoResponse appointmentDtoResponse = new AppointmentDtoResponse();
    private PatientDtoResponse patientDtoResponse  = new PatientDtoResponse();
    private DoctorDtoResponse doctorDtoResponse =  new DoctorDtoResponse();

    private PatientEntity patient = new PatientEntity();
    private DoctorEntity doctor = new DoctorEntity();

    @BeforeEach
    void setUp() {

        mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                .thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("admin");

        appointmentDtoRequest.setAppointmentDate(LocalDateTime.now());
        appointmentDtoRequest.setReason("Consultation");
        appointmentDtoRequest.setPatientId("1");
        appointmentDtoRequest.setDoctorId("1");

        appointmentEntity.setId("1");
        appointmentEntity.setAppointmentDate(LocalDateTime.now());
        appointmentEntity.setReason("Consultation");
        appointmentEntity.setPatientId("1");
        appointmentEntity.setDoctorId("1");
        appointmentEntity.setAppointmentNum("RDV-000001");
        appointmentEntity.setCreatedBy("david");
        appointmentEntity.setCreatedDate(LocalDateTime.now());
        appointmentEntity.setUpdatedDate(LocalDateTime.now());

        patient.setFirstName("Lucas");
        patient.setLastName("Bernard");

        doctor.setFirstName("Marie");
        doctor.setLastName("Martin");

        appointmentDtoResponse.setId("1");
        appointmentDtoResponse.setAppointmentDate(LocalDateTime.now());
        appointmentDtoResponse.setReason("Consultation");
        appointmentDtoResponse.setPatientId("1");
        appointmentDtoResponse.setPatientName("Lucas Bernard");
        appointmentDtoResponse.setDoctorId("1");
        appointmentDtoResponse.setDoctorId("1");
        appointmentDtoResponse.setDoctorName("Lucas Bernard");
        appointmentDtoResponse.setAppointmentNum("RDV-000001");

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

        doctorDtoResponse.setId("1");
        doctorDtoResponse.setFirstName("Marie");
        doctorDtoResponse.setLastName("Martin");
        doctorDtoResponse.setEmail("marie@gmail.com");
        doctorDtoResponse.setTelephone("771232211");
        doctorDtoResponse.setSpecialty("Dentist");
        doctorDtoResponse.setRegistrationNumber("DOC-000001");
    }


    @AfterEach
    void tearDown() {
        if (mockedSecurityContextHolder != null) {
            mockedSecurityContextHolder.close();
        }
    }


    @Test
    void saveAppointmentOK() {

        when(patientService.getPatientById("1")).thenReturn(Optional.of(patientDtoResponse));
        when(doctorService.getDoctorById("1")).thenReturn(Optional.of(doctorDtoResponse));
        when(appointmentMapper.toAppointmentEntity(appointmentDtoRequest)).thenReturn(appointmentEntity);


        long currentCount = 0L;
        when(appointmentRepository.count()).thenReturn(currentCount);

        long expectedSequence = currentCount +1 ;
        String expectedRegNumber = "RDV-000001";

        when(registrationNumber.generate("RDV", currentCount)).thenReturn(expectedRegNumber);
        when(appointmentRepository.save(any(AppointmentEntity.class))).thenReturn(appointmentEntity);
        when(appointmentMapper.toAppointmentDtoResponse(appointmentEntity)).thenReturn(appointmentDtoResponse);

        Optional<AppointmentDtoResponse> savedAppointment = appointmentService.saveAppointment(appointmentDtoRequest);

        assertThat(savedAppointment).isPresent();
        assertThat(savedAppointment.get()).isEqualTo(appointmentDtoResponse);

        verify(patientService).getPatientById("1");
        verify(doctorService).getDoctorById("1");
        verify(appointmentMapper).toAppointmentEntity(appointmentDtoRequest);
        //verify(registrationNumber).generate("RDV", expectedSequence);

        verify(appointmentMapper).toAppointmentDtoResponse(appointmentEntity);

        //assertTrue(savedAppointment.isPresent());
        assertEquals("1", savedAppointment.get().getId());
    }

//    @Test
//    void getAppointmentsOK() {
//        //PageRequest pageable = PageRequest.of(0, 1);
//        Pageable pageable = PageRequest.of(0, 1);
//        Page<AppointmentEntity> pageAppointmentEntity = new PageImpl<>(List.of(appointmentEntity));
//        when(appointmentRepository.findAll(pageable)).thenReturn(pageAppointmentEntity);
//        when(appointmentMapper.toAppointmentDtoResponseList(any())).thenReturn(List.of(appointmentDtoResponse));
//
//        Page<AppointmentDtoResponse> appointments = appointmentService.getAppointments(0, 1);
//        assertEquals(1, appointments.getContent().size());
//        assertEquals(appointmentDtoResponse.getId(), appointments.getContent().get(0).getId());
//    }

    @Test
    void getAppointmentsOK() {
        PageRequest pageable = PageRequest.of(0, 1);
        Page<AppointmentEntity> pageAppointmentEntity = new PageImpl<>(List.of(appointmentEntity));

        when(appointmentRepository.findAll(pageable)).thenReturn(pageAppointmentEntity);
        when(appointmentMapper.toAppointmentDtoResponse(appointmentEntity))
                .thenReturn(appointmentDtoResponse);

        when(patientService.getPatientById("1")).thenReturn(Optional.of(patientDtoResponse));
        when(doctorService.getDoctorById("1")).thenReturn(Optional.of(doctorDtoResponse));

        Page<AppointmentDtoResponse> appointments = appointmentService.getAppointments(0, 1);

        assertNotNull(appointments);
        assertEquals(1, appointments.getContent().size());
        assertEquals(appointmentDtoResponse.getId(), appointments.getContent().get(0).getId());

        List<AppointmentDtoResponse> content = appointments.getContent();
        assertEquals("Lucas Bernard", content.get(0).getPatientName());
        assertEquals("Marie Martin", content.get(0).getDoctorName());
    }

//    @Test
//    void getAppointments_shouldReturnPagedAndEnrichedAppointments() {
//
//        int pageNumber = 0;
//        int pageSize = 5;
//        Pageable pagedRequest = PageRequest.of(pageNumber, pageSize);
//
//        String patientId1 = UUID.randomUUID().toString();
//        String doctorId1 = UUID.randomUUID().toString();
//        String appointmentId1 = UUID.randomUUID().toString();
//
//        String patientId2 = UUID.randomUUID().toString();
//        String doctorId2 = UUID.randomUUID().toString();
//        String appointmentId2 = UUID.randomUUID().toString();
//
//        AppointmentEntity entity1 = new AppointmentEntity(appointmentId1, patientId1, doctorId1, LocalDateTime.now(), "Check-up", AppointmentStatus.SCHEDULED, "AP-001");
//        AppointmentEntity entity2 = new AppointmentEntity(appointmentId2, patientId2, doctorId2, LocalDateTime.now().plusDays(1), "Follow-up", AppointmentStatus.SCHEDULED, "AP-002");
//
//        List<AppointmentEntity> entityList = Arrays.asList(entity1, entity2);
//        Page<AppointmentEntity> pagedEntities = new PageImpl<>(entityList, pagedRequest, entityList.size());
//
//
//        AppointmentDtoResponse dto1 = new AppointmentDtoResponse();
//        dto1.setId(entity1.getId());
//        dto1.setPatientId(entity1.getPatientId());
//        dto1.setDoctorId(entity1.getDoctorId());
//        dto1.setAppointmentNum(entity1.getAppointmentNum());
//
//        AppointmentDtoResponse dto2 = new AppointmentDtoResponse();
//        dto2.setId(entity2.getId());
//        dto2.setPatientId(entity2.getPatientId());
//        dto2.setDoctorId(entity2.getDoctorId());
//        dto2.setAppointmentNum(entity2.getAppointmentNum());
//
//        // 4. Création des objets Patient et Doctor pour l'enrichissement
//        Patient patient1 = new Patient(patientId1, "John", "Doe");
//        Patient patient2 = new Patient(patientId2, "Jane", "Smith");
//        Doctor doctor1 = new Doctor(doctorId1, "Alice", "Raymond");
//        Doctor doctor2 = new Doctor(doctorId2, "Bob", "Marley");
//
//
//        when(appointmentRepository.findAll(pagedRequest)).thenReturn(pagedEntities);
//
//        // Quand le mapper est appelé, il retourne le DTO de base correspondant
//        when(appointmentMapper.toAppointmentDtoResponse(entity1)).thenReturn(dto1);
//        when(appointmentMapper.toAppointmentDtoResponse(entity2)).thenReturn(dto2);
//
//        // Quand les services sont appelés, ils retournent les objets Patient/Doctor
//        when(patientService.getPatientById(patientId1)).thenReturn(Optional.of(patient1));
//        when(patientService.getPatientById(patientId2)).thenReturn(Optional.of(patient2));
//        when(doctorService.getDoctorById(doctorId1)).thenReturn(Optional.of(doctor1));
//        when(doctorService.getDoctorById(doctorId2)).thenReturn(Optional.of(doctor2));
//
//        // --- ACT (When) ---
//        // Appel de la méthode que nous testons
//        Page<AppointmentDtoResponse> resultPage = appointmentService.getAppointments(pageNumber, pageSize);
//
//        // --- ASSERT (Then) ---
//        // Vérification des résultats et des interactions avec les mocks
//
//        // Vérifications sur la page
//        assertNotNull(resultPage);
//        assertEquals(2, resultPage.getTotalElements());
//        assertEquals(1, resultPage.getTotalPages());
//        List<AppointmentDtoResponse> resultList = resultPage.getContent();
//        assertEquals(2, resultList.size());
//
//        // Vérifications sur le premier objet enrichi
//        AppointmentDtoResponse resultDto1 = resultList.get(0);
//        assertEquals(appointmentId1, resultDto1.getId());
//        assertEquals("AP-001", resultDto1.getAppointmentNum()); // Vérifie qu'une donnée du mapper est conservée
//        assertEquals("John Doe", resultDto1.getPatientName()); // Vérifie l'enrichissement
//        assertEquals("Alice Raymond", resultDto1.getDoctorName()); // Vérifie l'enrichissement
//
//        // Vérifications sur le second objet enrichi
//        AppointmentDtoResponse resultDto2 = resultList.get(1);
//        assertEquals(appointmentId2, resultDto2.getId());
//        assertEquals("AP-002", resultDto2.getAppointmentNum());
//        assertEquals("Jane Smith", resultDto2.getPatientName());
//        assertEquals("Bob Marley", resultDto2.getDoctorName());
//
//        // Vérification que les dépendances ont été appelées comme prévu
//        verify(appointmentRepository, times(1)).findAll(pagedRequest);
//        verify(patientService, times(1)).getPatientById(patientId1);
//        verify(patientService, times(1)).getPatientById(patientId2);
//        verify(doctorService, times(1)).getDoctorById(doctorId1);
//        verify(doctorService, times(1)).getDoctorById(doctorId2);
//        verify(appointmentMapper, times(2)).toAppointmentDtoResponse(any(AppointmentEntity.class));
//
//        // On peut même vérifier qu'aucune autre interaction n'a eu lieu
//        verifyNoMoreInteractions(patientService, doctorService, appointmentRepository, appointmentMapper);
//    }

    @Test
    void getAppointmentByIdOK() {
        when(appointmentRepository.findById(anyString())).thenReturn(Optional.of(appointmentEntity));
        when(appointmentMapper.toAppointmentDtoResponse(any())).thenReturn(appointmentDtoResponse);

        Optional<AppointmentDtoResponse> appointment = appointmentService.getAppointmentById("1");
        assertTrue(appointment.isPresent());
        assertEquals("1", appointment.get().getId());
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
        when(appointmentRepository.findById(anyString())).thenReturn(Optional.of(appointmentEntity));
        when(appointmentRepository.save(any())).thenReturn(appointmentEntity);
        when(appointmentMapper.toAppointmentDtoResponse(any())).thenReturn(appointmentDtoResponse);

        Optional<AppointmentDtoResponse> updatedAppointment = appointmentService.updateAppointment("1", appointmentDtoRequest);
        assertTrue(updatedAppointment.isPresent());
        assertEquals("1", updatedAppointment.get().getId());
    }

    @Test
    void updateAppointmentKO() {
        when(appointmentRepository.findById(anyString())).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("appointment.notfound"), any(), any(Locale.class)))
                .thenReturn("Appointment not found");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> appointmentService.updateAppointment("1" ,appointmentDtoRequest));
        assertEquals("Appointment not found", exception.getMessage());
    }

    @Test
    void deleteAppointmentOK() {
        when(appointmentRepository.findById(anyString())).thenReturn(Optional.of(appointmentEntity));
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

//    private AppointmentDtoRequest getAppointmentDtoRequest(){
//        AppointmentDtoRequest appointmentDtoRequest = new AppointmentDtoRequest();
//        appointmentDtoRequest.setAppointmentDate(LocalDateTime.now());
//        appointmentDtoRequest.setReason("Consultation");
//        appointmentDtoRequest.setPatientId("1");
//        appointmentDtoRequest.setDoctorId("1");
//        return appointmentDtoRequest;
//    }
//    private AppointmentEntity getAppointmentEntity(){
//        AppointmentEntity appointmentEntity = new AppointmentEntity();
//        appointmentEntity.setId("1");
//        appointmentEntity.setAppointmentDate(LocalDateTime.now());
//        appointmentEntity.setReason("Consultation");
//        appointmentEntity.setPatientId("1");
//        appointmentEntity.setDoctorId("1");
//        appointmentEntity.setAppointmentNum("APT-000001");
//        appointmentEntity.setCreatedBy("david");
//        appointmentEntity.setCreatedDate(LocalDateTime.now());
//        appointmentEntity.setUpdatedDate(LocalDateTime.now());
//        return appointmentEntity;
//    }
//    private AppointmentDtoResponse getAppointmentDtoResponse(){
//        AppointmentDtoResponse appointmentDtoResponse = new AppointmentDtoResponse();
//        appointmentDtoResponse.setId("1");
//        appointmentDtoResponse.setAppointmentDate(LocalDateTime.now());
//        appointmentDtoResponse.setReason("Consultation");
//        appointmentDtoResponse.setPatientId("1");
//        appointmentDtoResponse.setPatientName("Lucas Bernard");
//        appointmentDtoResponse.setDoctorId("1");
//        appointmentDtoResponse.setDoctorId("1");
//        appointmentDtoResponse.setDoctorName("Lucas Bernard");
//        appointmentDtoResponse.setAppointmentNum("APT-000001");
//        return appointmentDtoResponse;
//    }
//
//
//    private PatientDtoResponse getPatientDtoResponse(){
//        PatientDtoResponse patientDtoResponse = new PatientDtoResponse();
//        patientDtoResponse.setId("1");
//        patientDtoResponse.setFirstName("Lucas");
//        patientDtoResponse.setLastName("Bernard");
//        patientDtoResponse.setBirthDate(LocalDate.of(1985,7,22));
//        patientDtoResponse.setPlaceOfBirth("Paris");
//        patientDtoResponse.setGender(Gender.MALE);
//        patientDtoResponse.setPhoneNumber("771232211");
//        patientDtoResponse.setAddress("15 Rue de la Paix, 75002 Paris");
//        patientDtoResponse.setPersonToNotifyName("Marie Dupont");
//        patientDtoResponse.setPersonToNotifyPhoneNumber("0798765432");
//        patientDtoResponse.setRegistrationNumber("PAT-000001");
//        return patientDtoResponse;
//    }
//
//    private DoctorDtoResponse getDoctorDtoResponse(){
//        DoctorDtoResponse doctorDtoResponse = new DoctorDtoResponse();
//        doctorDtoResponse.setId("1");
//        doctorDtoResponse.setFirstName("Lucas");
//        doctorDtoResponse.setLastName("Bernard");
//        doctorDtoResponse.setEmail("lucas@gmail.com");
//        doctorDtoResponse.setTelephone("771232211");
//        doctorDtoResponse.setSpecialty("Dentist");
//        doctorDtoResponse.setRegistrationNumber("DOC-000001");
//        return doctorDtoResponse;
//    }

    private void mockSecurityContext() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
                     Mockito.mockStatic(SecurityContextHolder.class)) {

            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(jwt);
            when(jwt.getClaimAsString("preferred_username")).thenReturn("admin");
        }
    }
}
