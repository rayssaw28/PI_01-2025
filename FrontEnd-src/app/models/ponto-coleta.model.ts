import { Bairro } from './bairro.model';
import { TipoResiduo } from './tipo-residuo.enum';

export interface PontoColeta {
  id?: number;
  nome: string;
  responsavel: string;
  contato: string;
  endereco: string;
  bairro?: Bairro; // Objeto completo é opcional
  tiposResiduos: TipoResiduo[];
}