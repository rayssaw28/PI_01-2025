import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { ErrorModalService } from './error-modal.service';
import { Bairro } from '../models/bairro.model';

@Injectable({
  providedIn: 'root'
})
export class BairroService {
  private apiUrl = 'http://localhost:8080/greenlog/api/bairro';

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
    let userMessage = error.error?.error || 'Ocorreu um erro inesperado ao processar bairros.';
    this.errorModalService.showError(userMessage);
    return throwError(() => new Error(userMessage));
  }

  listarBairros(): Observable<Bairro[]> {
    return this.http.get<Bairro[]>(this.apiUrl, { headers: this.getAuthHeaders() })
      .pipe(catchError(this.handleError.bind(this)));
  }

  salvarBairro(bairro: Bairro): Observable<Bairro> {
    // Se o bairro tem ID, é uma atualização (PUT), senão é uma criação (POST)
    if (bairro.id) {
      return this.http.put<Bairro>(`${this.apiUrl}/${bairro.id}`, bairro, { headers: this.getAuthHeaders() })
        .pipe(catchError(this.handleError.bind(this)));
    } else {
      return this.http.post<Bairro>(this.apiUrl, bairro, { headers: this.getAuthHeaders() })
        .pipe(catchError(this.handleError.bind(this)));
    }
  }

  excluirBairro(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() })
      .pipe(catchError(this.handleError.bind(this)));
  }
}