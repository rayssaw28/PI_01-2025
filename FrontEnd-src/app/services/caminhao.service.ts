import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Caminhao } from '../models/caminhao.model';
import { AuthService } from './auth.service';
import { ErrorModalService } from './error-modal.service';

@Injectable({
  providedIn: 'root'
})
export class CaminhaoService {

  // A URL base está correta, usando o plural "caminhoes".
  private apiUrl = 'http://localhost:8080/greenlog/api/caminhoes';

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

  private handleError(error: HttpErrorResponse) {
    let userMessage = 'Ocorreu um erro inesperado. Tente novamente.';
    if (error.error && typeof error.error.error === 'string') {
        userMessage = error.error.error;
    } else if (error.status === 404) {
        userMessage = 'O recurso solicitado não foi encontrado no servidor.';
    }
    this.errorModalService.showError(userMessage);
    return throwError(() => new Error(userMessage));
  }

  listarCaminhoes(): Observable<Caminhao[]> {
    return this.http.get<Caminhao[]>(this.apiUrl, { headers: this.getAuthHeaders() })
      .pipe(catchError(this.handleError.bind(this)));
  }

  salvarCaminhao(caminhao: Caminhao): Observable<Caminhao> {
    return this.http.post<Caminhao>(this.apiUrl, caminhao, { headers: this.getAuthHeaders() })
      .pipe(catchError(this.handleError.bind(this)));
  }

  // --- AQUI ESTÁ A CORREÇÃO ---
  // A URL agora é construída corretamente usando template literals.
  atualizarCaminhao(id: number, caminhao: Caminhao): Observable<Caminhao> {
    const url = `${this.apiUrl}/${id}`; // Exemplo: /api/caminhoes/1
    return this.http.put<Caminhao>(url, caminhao, { headers: this.getAuthHeaders() })
      .pipe(catchError(this.handleError.bind(this)));
  }

  // --- E AQUI TAMBÉM ---
  excluirCaminhao(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`; // Exemplo: /api/caminhoes/1
    return this.http.delete<void>(url, { headers: this.getAuthHeaders() })
      .pipe(catchError(this.handleError.bind(this)));
  }
}
