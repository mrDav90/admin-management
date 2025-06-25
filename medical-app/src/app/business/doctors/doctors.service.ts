import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { PaginatedResponse } from '../../shared/models/paginated-response';
import { DoctorRequest } from './models/doctor-request';
import { DoctorResponse } from './models/doctor-response';

@Injectable({
  providedIn: 'root'
})
export class DoctorsService {

  constructor() { }

  private http = inject(HttpClient);
    
      _createDoctor(payload: DoctorRequest): Observable<DoctorResponse> {
        return this.http.post<DoctorResponse>(
          `${environment.apiUrl}/api/v1/doctors`,
          payload
        );
      }
    
      _getDoctors(pageNumber : number, pageSize : number): Observable<PaginatedResponse<DoctorResponse>> {
        return this.http.get<PaginatedResponse<DoctorResponse>>(
          `${environment.apiUrl}/api/v1/doctors?pageNumber=${pageNumber - 1}&pageSize=${pageSize}`
        );
      }
    
      _updateDoctor(id: string, payload: DoctorRequest): Observable<DoctorResponse> {
        return this.http.put<DoctorResponse>(
          `${environment.apiUrl}/api/v1/doctors/${id}`,
          payload
        );
      }
    
      _deleteDoctor(id: string) {
        return this.http.delete(`${environment.apiUrl}/api/v1/doctors/${id}`);
      }
}

//text