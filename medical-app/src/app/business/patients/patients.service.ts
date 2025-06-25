import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { PaginatedResponse } from '../../shared/models/paginated-response';
import { PatientRequest } from './models/patient-request';
import { PatientResponse } from './models/patient-response';

@Injectable({
  providedIn: 'root'
})
export class PatientsService {

  constructor() { }
  private http = inject(HttpClient);
  
    _createPatient(payload: PatientRequest): Observable<PatientResponse> {
      return this.http.post<PatientResponse>(
        `${environment.apiUrl}/api/v1/patients`,
        payload
      );
    }
  
    _getPatients(pageNumber : number, pageSize : number): Observable<PaginatedResponse<PatientResponse>> {
      return this.http.get<PaginatedResponse<PatientResponse>>(
        `${environment.apiUrl}/api/v1/patients?pageNumber=${pageNumber - 1}&pageSize=${pageSize}`
      );
    }
  
    _updatePatient(id: string, payload: PatientRequest): Observable<PatientResponse> {
      return this.http.put<PatientResponse>(
        `${environment.apiUrl}/api/v1/patients/${id}`,
        payload
      );
    }
  
    _deletePatient(id: string) {
      return this.http.delete(`${environment.apiUrl}/api/v1/patients/${id}`);
    }
}
