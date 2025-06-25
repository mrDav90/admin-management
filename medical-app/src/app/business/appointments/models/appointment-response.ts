import { Gender } from "../../../shared/enums/gender";
import { AppointmentStatus } from "./appointment-status";

export interface AppointmentResponse {
    id: string;
    appointmentDate: Date;
    reason: string;
    status: AppointmentStatus;
    patientId: string;
    doctorId: string;
    doctorName: string;
    patientName: string;
    appointmentNum: string;
}