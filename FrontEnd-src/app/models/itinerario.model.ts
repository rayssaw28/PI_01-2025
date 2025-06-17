// src/app/models/itinerario.model.ts
import { Caminhao } from './caminhao.model';
import { Rota } from './rota.model';

export interface Itinerario {
  id: number;
  data: string;
  rota: Rota;
  caminhao: Caminhao;
}