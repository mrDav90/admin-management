import { Gender } from "../../../shared/enums/gender";

export interface AppointmentRequest {
    appointmentDate: Date;
    reason ?: string;
    patientId: string;
    doctorId: string;
    
}