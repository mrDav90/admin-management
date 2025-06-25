import { Gender } from "../../../shared/enums/gender";

export interface PatientRequest {
    firstName: string;
    lastName: string;
    birthDate: Date;
    placeOfBirth: string;
    gender: Gender;
    phoneNumber: string;
    address: string;
    personToNotifyName ?: string;
    personToNotifyPhoneNumber ?: string;
}