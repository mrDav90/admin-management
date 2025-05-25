package com.si.admin_management.services.appointments;

import com.si.admin_management.dtos.appointments.AppointmentDtoResponse;
import com.si.admin_management.dtos.appointments.AppointmentDtoRequest;
import com.si.admin_management.entities.AppointmentEntity;
import com.si.admin_management.entities.AppointmentStatus;
import com.si.admin_management.exception.EntityExistsException;
import com.si.admin_management.exception.EntityNotFoundException;
import com.si.admin_management.mappers.AppointmentMapper;
import com.si.admin_management.repositories.AppointmentRepository;
import com.si.admin_management.services.doctors.IDoctorService;
import com.si.admin_management.services.patients.IPatientService;
import com.si.admin_management.utils.RegistrationNumber;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements IAppointmentService{
    private final AppointmentRepository appointmentRepository;
    private final IPatientService patientService;
    private final IDoctorService doctorService;
    private final AppointmentMapper appointmentMapper;
    private final MessageSource messageSource;
    private final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final RegistrationNumber registrationNumber = new RegistrationNumber();

    @Override
    public Optional<AppointmentDtoResponse> saveAppointment(AppointmentDtoRequest appointmentDtoRequest){

        if (patientService.getPatientById(appointmentDtoRequest.getPatientId()).isEmpty()){
            throw new EntityNotFoundException(messageSource.getMessage("patient.notfound", new Object[]{appointmentDtoRequest.getPatientId()}, Locale.getDefault()));
        }
        if (doctorService.getDoctorById(appointmentDtoRequest.getDoctorId()).isEmpty()){
            throw new EntityNotFoundException(messageSource.getMessage("doctor.notfound", new Object[]{appointmentDtoRequest.getDoctorId()}, Locale.getDefault()));
        }
        AppointmentEntity appointment = appointmentMapper.toAppointmentEntity(appointmentDtoRequest);
        appointment.setAppointmentNum(registrationNumber.generate("RDV" , appointmentRepository.count()));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setCreatedDate(LocalDateTime.now());



        var user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("User principal : {}" , user);
//        appointment.setCreatedBy();
//        logger.info("Appointment : {}", appointment);
//
//        AppointmentEntity appointmentEntity = appointmentRepository.save(appointment);
//        AppointmentDtoResponse appointmentDtoResponse = appointmentMapper.toAppointmentDtoResponse(appointmentEntity);
//        return Optional.of(appointmentDtoResponse);
        return null;
    }


    @Override
    public Page<AppointmentDtoResponse> getAppointments(int pageNumber , int pageSize){
        Pageable pagedRequest = PageRequest.of(pageNumber,pageSize);
        Page<AppointmentEntity> appointmentsEntities = appointmentRepository.findAll(pagedRequest);
        return appointmentsEntities.map(item -> {
            AppointmentDtoResponse appointmentDtoResponse = appointmentMapper.toAppointmentDtoResponse(item);
            var patient = patientService.getPatientById(item.getPatientId()).get();
            var doctor = doctorService.getDoctorById(item.getDoctorId()).get();
            appointmentDtoResponse.setPatientName(patient.getFirstName()+" "+patient.getLastName());
            appointmentDtoResponse.setDoctorName(doctor.getFirstName()+" "+doctor.getLastName());
            return appointmentDtoResponse;
        });
    }

    @Override
    public Optional<AppointmentDtoResponse> getAppointmentById(String id){
        return appointmentRepository.findById(id)
                .map(appointment -> Optional.of(appointmentMapper.toAppointmentDtoResponse(appointment)))
                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("appointment.notfound", new Object[]{id}, Locale.getDefault())));
    }

    @Override
    public Optional<AppointmentDtoResponse> updateAppointment(String id, AppointmentDtoRequest appointmentDtoRequest){
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointment.setAppointmentDate(appointmentDtoRequest.getAppointmentDate());
                    appointment.setReason(appointmentDtoRequest.getReason());
                    appointment.setPatientId(appointmentDtoRequest.getPatientId());
                    appointment.setDoctorId(appointmentDtoRequest.getDoctorId());
                    appointment.setUpdatedDate(LocalDateTime.now());


                    var appointmentEntity = appointmentRepository.save(appointment);
                    return Optional.of(appointmentMapper.toAppointmentDtoResponse(appointmentEntity));
                }).orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("appointment.notfound", new Object[]{id}, Locale.getDefault())));
    }

    @Override
    public boolean deleteAppointment(String id){
        var appointment = appointmentRepository.findById(id);
        if (appointment.isEmpty()) {
            throw new EntityNotFoundException(messageSource.getMessage("appointment.notfound", new Object[]{id}, Locale.getDefault()));
        }
        appointmentRepository.deleteById(id);
        return true;
    }
}
