import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { ErrorModalService } from './error-modal.service';
import { Bairro } from '../models/bairro.model';
import { Rota } from '../models/rota.model';
import { Itinerario } from '../models/itinerario.model';
import { TipoResiduo } from '../models/tipo-residuo.enum';

@Injectable({
  providedIn: 'root'
})
export class RotaService {
  private apiUrl = 'http://localhost:8080/greenlog/api/rotas';
  private bairroApiUrl = 'http://localhost:8080/greenlog/api/bairros'; // Ajustado para plural, se aplicável no backend

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private errorModalService: ErrorModalService
  ) {}

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? token : ''
    });
  }

  private handleError(error: HttpErrorResponse) {
    const userMessage = error.error?.error || 'Ocorreu um erro na operação de rotas.';
    this.errorModalService.showError(userMessage);
    return throwError(() => new Error(userMessage));
  }

  // Métodos existentes
  listarRotas(): Observable<Rota[]> {
    return this.http.get<Rota[]>(this.apiUrl, { headers: this.getAuthHeaders() }).pipe(catchError(this.handleError.bind(this)));
  }

  listarBairros(): Observable<Bairro[]> {
    // Certifique-se de que a URL corresponde ao seu BairroController
    return this.http.get<Bairro[]>(this.bairroApiUrl, { headers: this.getAuthHeaders() }).pipe(catchError(this.handleError.bind(this)));
  }
  
  // --- NOVO MÉTODO PARA A ROTA INTELIGENTE ---
  /**
   * Chama o endpoint do backend para calcular a rota inteligente e agendar o itinerário.
   */
  calcularEAgendarRotaInteligente(
    pontoOrigemId: number,
    pontoDestinoId: number,
    caminhaoId: number,
    tipoResiduo: TipoResiduo,
    data: string
  ): Observable<Itinerario> {
    
    // Constrói os parâmetros da URL
    let params = new HttpParams()
      .set('pontoOrigemId', pontoOrigemId.toString())
      .set('pontoDestinoId', pontoDestinoId.toString())
      .set('caminhaoId', caminhaoId.toString())
      .set('tipoResiduo', tipoResiduo)
      .set('data', data);

    // Constrói a URL final com o endpoint correto
    const url = `${this.apiUrl}/calcular-inteligente`;

    // Faz a requisição POST
    return this.http.post<Itinerario>(url, null, { headers: this.getAuthHeaders(), params: params })
      .pipe(
        catchError(this.handleError.bind(this))
      );
  }

  // O método antigo pode ser removido ou mantido, caso ainda o use em algum lugar.
  // calcularEAgendarRota(origemId: number, destinoId: number, caminhaoId: number, tipoResiduo: TipoResiduo, data: string): Observable<Itinerario> {
  //   ... lógica antiga ...
  // }
}
