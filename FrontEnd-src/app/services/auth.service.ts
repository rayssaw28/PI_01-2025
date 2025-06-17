import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ErrorModalService } from './error-modal.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/greenlog';
  private isLoggedInSubject = new BehaviorSubject<boolean>(this.hasToken());

  isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor(private http: HttpClient, private errorModalService: ErrorModalService) { }

  private hasToken(): boolean {
    return !!sessionStorage.getItem('auth_token');
  }

  login(username: string, password: string): Observable<boolean> {
    const authString = 'Basic ' + btoa(username + ':' + password);
    const headers = new HttpHeaders({
      'Authorization': authString
    });

    return this.http.get<any>(`${this.apiUrl}/api/caminhoes`, { headers: headers, observe: 'response' }).pipe(
      map((response: HttpResponse<any>) => {
        if (response.ok) {
          sessionStorage.setItem('auth_token', authString);
          sessionStorage.setItem('username', username);
          this.isLoggedInSubject.next(true);
          return true;
        }
        return false;
      }),
      catchError(error => {
        if (error.status === 401) {
            this.errorModalService.showError("Usuário ou senha inválidos. Por favor, tente novamente.");
        } else {
            this.errorModalService.showError("Não foi possível conectar ao servidor. Verifique sua conexão ou tente mais tarde.");
        }
        this.isLoggedInSubject.next(false);
        return of(false);
      })
    );
  }

  logout(): void {
    sessionStorage.removeItem('auth_token');
    sessionStorage.removeItem('username');
    this.isLoggedInSubject.next(false);
  }

  getToken(): string | null {
    return sessionStorage.getItem('auth_token');
  }
}