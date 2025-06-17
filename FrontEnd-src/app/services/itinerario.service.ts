import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { ErrorModalService } from './error-modal.service';
import { Itinerario } from '../models/itinerario.model';

@Injectable({ providedIn: 'root' })
export class ItinerarioService {
  // CORREÇÃO: URL base agora no plural, para corresponder ao backend corrigido.
  private apiUrl = 'http://localhost:8080/greenlog/api/itinerarios';

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
    // Extrai a mensagem de erro específica do backend, se existir.
    const userMessage = error.error?.error || 'Ocorreu um erro ao processar sua solicitação.';
    this.errorModalService.showError(userMessage);
    return throwError(() => new Error(userMessage));
  }

  /**
   * Método central e único para listar itinerários com base em filtros opcionais.
   * @param caminhaoId ID do caminhão (pode ser null ou 0 para "todos").
   * @param mes Mês para filtrar (pode ser null).
   * @param ano Ano para filtrar (pode ser null).
   * @param motorista Nome do motorista para filtrar (pode ser null ou string vazia).
   */
  listarItinerariosComFiltros(
    caminhaoId: number | null,
    mes: number | null,
    ano: number | null,
    motorista: string | null
  ): Observable<Itinerario[]> {
    
    let params = new HttpParams();

    // Adiciona os parâmetros à requisição apenas se eles tiverem um valor válido.
    if (caminhaoId && caminhaoId !== 0) {
      params = params.set('caminhaoId', caminhaoId.toString());
    }
    if (mes) {
      params = params.set('mes', mes.toString());
    }
    if (ano) {
      params = params.set('ano', ano.toString());
    }
    if (motorista && motorista.trim() !== '') {
      params = params.set('motorista', motorista.trim());
    }

    // A chamada GET é feita para a URL base, com os parâmetros de filtro.
    return this.http.get<Itinerario[]>(this.apiUrl, { headers: this.getAuthHeaders(), params: params })
      .pipe(
        catchError(this.handleError.bind(this))
      );
  }

  // OBS: Os métodos antigos de busca foram removidos para evitar inconsistências.
  // Mantenha apenas o 'listarItinerariosComFiltros' para todas as buscas.
}
