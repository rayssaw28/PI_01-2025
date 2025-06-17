
import { TipoResiduo } from './tipo-residuo.enum';

export interface Caminhao {
  id?: number; // Opcional porque não terá ID ao criar um novo
  placa: string;
  motorista: string;
  capacidade: number;
  tiposResiduos: TipoResiduo[]; // Array de ENUMs
}