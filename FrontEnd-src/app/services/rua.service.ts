import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { ErrorModalService } from './error-modal.service';
import { Rua } from '../models/rua.model';

@Injectable({
  providedIn: 'root'
})
export class RuaService {
  private apiUrl = 'http://localhost:8080/greenlog/api/rua';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private errorModalService: ErrorModalService
  ) { }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? token : ''
    });
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let userMessage = error.error?.error || 'Ocorreu um erro inesperado ao processar ruas.';
    this.errorModalService.showError(userMessage);
    return throwError(() => new Error(userMessage));
  }

  listarRuas(): Observable<Rua[]> {
    return this.http.get<Rua[]>(this.apiUrl, { headers: this.getAuthHeaders() })
      .pipe(catchError(this.handleError.bind(this)));
  }

  salvarRua(rua: any): Observable<Rua> { // 'any' aqui pois o payload tem a estrutura aninhada
    // Se a rua tem ID, é uma atualização (PUT), senão é uma criação (POST)
    if (rua.id) {
      return this.http.put<Rua>(`${this.apiUrl}/${rua.id}`, rua, { headers: this.getAuthHeaders() })
        .pipe(catchError(this.handleError.bind(this)));
    } else {
      return this.http.post<Rua>(this.apiUrl, rua, { headers: this.getAuthHeaders() })
        .pipe(catchError(this.handleError.bind(this)));
    }
  }

  excluirRua(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() })
      .pipe(catchError(this.handleError.bind(this)));
  }
}