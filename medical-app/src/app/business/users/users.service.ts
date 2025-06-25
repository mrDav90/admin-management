import { inject, Injectable } from '@angular/core';
import { UserRequest } from './models/user-request';
import { UserResponse } from './models/user-response';
import { PaginatedResponse } from '../../shared/models/paginated-response';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  constructor() {}
  private http = inject(HttpClient);

  _createUser(payload: UserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(
      `${environment.apiUrl}/api/v1/users`,
      payload
    );
  }

  _getUsers(pageNumber : number, pageSize : number): Observable<PaginatedResponse<UserResponse>> {
    return this.http.get<PaginatedResponse<UserResponse>>(
      `${environment.apiUrl}/api/v1/users?pageNumber=${pageNumber - 1}&pageSize=${pageSize}`
    );
  }

  _updateUser(id: string, payload: UserRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(
      `${environment.apiUrl}/api/v1/users/${id}`,
      payload
    );
  }

  _deleteUser(id: string) {
    return this.http.delete(`${environment.apiUrl}/api/v1/users/${id}`);
  }
}
