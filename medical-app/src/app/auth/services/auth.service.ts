import { inject, Injectable } from '@angular/core';
import { UserInfos } from '../models/UserInfos';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor() {}

  private http = inject(HttpClient);

  _getMe(): Observable<UserInfos> {
    return this.http.get<UserInfos>(`${environment.apiUrl}/api/v1/users/me`);
  }
}
