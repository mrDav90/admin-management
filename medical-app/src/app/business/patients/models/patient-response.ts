import { Gender } from "../../../shared/enums/gender";

export interface PatientResponse {
    id: string;
    firstName: string;
    lastName: string;
    birthDate: Date;
    placeOfBirth: string;
    gender: Gender;
    phoneNumber: string;
    address: string;
    registrationNumber: string;
    personToNotifyName ?: string;
    personToNotifyPhoneNumber ?: string;
}