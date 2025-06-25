import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { PaginatedResponse } from '../../shared/models/paginated-response';
import { AppointmentRequest } from './models/appointment-request';
import { AppointmentResponse } from './models/appointment-response';

@Injectable({
  providedIn: 'root'
})
export class AppointmentsService {

  constructor() { }

  private http = inject(HttpClient);
    
      _createAppointment(payload: AppointmentRequest): Observable<AppointmentResponse> {
        return this.http.post<AppointmentResponse>(
          `${environment.apiUrl}/api/v1/appointments`,
          payload
        );
      }
    
      _getAppointments(pageNumber : number, pageSize : number): Observable<PaginatedResponse<AppointmentResponse>> {
        return this.http.get<PaginatedResponse<AppointmentResponse>>(
          `${environment.apiUrl}/api/v1/appointments?pageNumber=${pageNumber - 1}&pageSize=${pageSize}`
        );
      }

      _getAppointmentById(id : string): Observable<AppointmentResponse> {
        return this.http.get<AppointmentResponse>(
          `${environment.apiUrl}/api/v1/appointments/${id}`
        );
      }
    
      _updateAppointment(id: string, payload: AppointmentRequest): Observable<AppointmentResponse> {
        return this.http.put<AppointmentResponse>(
          `${environment.apiUrl}/api/v1/appointments/${id}`,
          payload
        );
      }
    
      _deleteAppointment(id: string) {
        return this.http.delete(`${environment.apiUrl}/api/v1/appointments/${id}`);
      }

      
}

