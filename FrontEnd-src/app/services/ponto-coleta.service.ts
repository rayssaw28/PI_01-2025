import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { ErrorModalService } from './error-modal.service';
import { PontoColeta } from '../models/ponto-coleta.model';

@Injectable({
  providedIn: 'root'
})
export class PontoColetaService {
  // CORREÇÃO: Usando a URL no plural para seguir as melhores práticas.
  // Lembre-se de ajustar o seu PontoColetaController no backend para @RequestMapping("/api/pontosColeta")
  private apiUrl = 'http://localhost:8080/greenlog/api/pontosColeta';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private errorModalService: ErrorModalService
  ) { }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    // Garante que o Content-Type seja sempre definido.
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? token : ''
    });
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    const userMessage = error.error?.error || 'Ocorreu um erro inesperado ao processar o ponto de coleta.';
    this.errorModalService.showError(userMessage);
    return throwError(() => new Error(userMessage));
  }

  listarPontosColeta(): Observable<PontoColeta[]> {
    return this.http.get<PontoColeta[]>(this.apiUrl, { headers: this.getAuthHeaders() })
      .pipe(catchError(this.handleError.bind(this)));
  }

  /**
   * Salva ou atualiza um Ponto de Coleta.
   * Este método agora é um só, e a lógica de POST vs PUT é feita aqui dentro.
   */
  salvarPontoColeta(pontoColeta: Partial<PontoColeta>): Observable<PontoColeta> {
    const headers = this.getAuthHeaders();
    
    // Se o pontoColeta tem um ID, é uma atualização (PUT).
    if (pontoColeta.id) {
      const url = `${this.apiUrl}/${pontoColeta.id}`;
      return this.http.put<PontoColeta>(url, pontoColeta, { headers })
        .pipe(catchError(this.handleError.bind(this)));
    } 
    // Se não tem ID, é uma criação (POST).
    else {
      return this.http.post<PontoColeta>(this.apiUrl, pontoColeta, { headers })
        .pipe(catchError(this.handleError.bind(this)));
    }
  }

  excluirPontoColeta(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url, { headers: this.getAuthHeaders() })
      .pipe(catchError(this.handleError.bind(this)));
  }
}
