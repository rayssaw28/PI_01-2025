import { Bairro } from './bairro.model';
import { Caminhao } from './caminhao.model';
import { TipoResiduo } from './tipo-residuo.enum';
export interface Rota {
  id: number; distanciaTotal: number; caminhao: Caminhao;
  bairros: Bairro[]; tipoResiduo: TipoResiduo;
}