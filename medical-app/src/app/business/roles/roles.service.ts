import { inject, Injectable } from '@angular/core';
import { RoleRequest } from './models/role-request';
import { RoleResponse } from './models/role-response';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { PaginatedResponse } from '../../shared/models/paginated-response';
import { AssignPermission } from './models/assign-permission';
import { AppPermission } from './models/app-permission';

@Injectable({
  providedIn: 'root'
})
export class RolesService {

  constructor() { }

  private http = inject(HttpClient);
  
    _createRole(payload: RoleRequest): Observable<RoleResponse> {
      return this.http.post<RoleResponse>(
        `${environment.apiUrl}/api/v1/roles`,
        payload
      );
    }
  
    _getRoles(pageNumber : number, pageSize : number): Observable<PaginatedResponse<RoleResponse>> {
      return this.http.get<PaginatedResponse<RoleResponse>>(
        `${environment.apiUrl}/api/v1/roles?pageNumber=${pageNumber - 1}&pageSize=${pageSize}`
      );
    }

    _getAllPermissions(): Observable<AppPermission[]> {
      return this.http.get<AppPermission[]>(
        `${environment.apiUrl}/api/v1/permissions`
      );
    }
  
    _assignPermissions(roleName : string, payload: AssignPermission) {
      return this.http.put(
        `${environment.apiUrl}/api/v1/roles/${roleName}/permissions`,
        payload
      );
    }

    _getPermissionsByRole(roleName : string): Observable<string[]> {
      return this.http.get<string[]>(
        `${environment.apiUrl}/api/v1/permissions/${roleName}`
      );
    }
    
}
