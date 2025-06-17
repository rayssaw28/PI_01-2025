import { Bairro } from './bairro.model';

export interface Rua {
  id?: number;
  bairroOrigem?: Bairro;  // Objeto completo é opcional
  bairroDestino?: Bairro; // Objeto completo é opcional
  distancia: number;
}