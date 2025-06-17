// src/app/app.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { Subscription } from 'rxjs';

// Componentes e Serviços
import { ErrorModalComponent } from './components/error-modal/error-modal.component';
import { AuthService } from './services/auth.service';
import { CaminhaoService } from './services/caminhao.service';
import { RotaService } from './services/rota.service';
import { ItinerarioService } from './services/itinerario.service';
import { ErrorModalService } from './services/error-modal.service';
import { BairroService } from './services/bairro.service';
import { RuaService } from './services/rua.service';
import { PontoColetaService } from './services/ponto-coleta.service';

// Modelos (Interfaces)
import { Caminhao } from './models/caminhao.model';
import { TipoResiduo } from './models/tipo-residuo.enum';
import { Bairro } from './models/bairro.model';
import { Rota } from './models/rota.model';
import { Itinerario } from './models/itinerario.model';
import { Rua } from './models/rua.model';
import { PontoColeta } from './models/ponto-coleta.model';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, ErrorModalComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  // --- Estado Geral ---
  isLoggedIn: boolean = false;
  currentView: 'login' | 'menu' | 'caminhoes' | 'itinerarios' | 'rotas' | 'areas' = 'login';
  username!: string;
  password!: string;

  // --- Erros ---
  errorMessage: string | null = null;
  private errorSubscription!: Subscription;

  // --- Caminhões ---
  caminhoes: Caminhao[] = [];
  mostrarModalCaminhao: boolean = false;
  caminhaoParaEdicao: Caminhao | null = null;
  caminhao: Partial<Caminhao> = { placa: '', motorista: '', capacidade: 0, tiposResiduos: [] };

  // --- Rotas (ATUALIZADO PARA FILTROS DINÂMICOS) ---
  rotas: Rota[] = [];
  pontosDeColetaParaRota: PontoColeta[] = []; // Lista completa de todos os pontos
  rotaParaCalculo = { 
    pontoOrigemId: null as number | null, 
    pontoDestinoId: null as number | null, 
    tipoResiduo: null as TipoResiduo | null,
    caminhaoId: null as number | null, 
    data: '' 
  };

  pontosDestinoFiltrados: PontoColeta[] = [];
  residuosEmComum: TipoResiduo[] = [];
  caminhoesFiltrados: Caminhao[] = [];


  itinerarios: Itinerario[] = [];
  filtroItinerario = { caminhaoId: 0, mes: null as number | null, ano: null as number | null, motorista: '' };

  currentAreaSubView: 'bairros' | 'ruas' | 'pontosColeta' | null = null;
  bairros: Bairro[] = [];


  mostrarModalBairro: boolean = false;
  bairroParaCadastro: Partial<Bairro> = { nome: '' };


  ruas: Rua[] = [];
  mostrarModalRua: boolean = false;
  ruaParaCadastro: Partial<Rua> & { bairroOrigemId: number | null, bairroDestinoId: number | null } = {
    distancia: 0,
    bairroOrigemId: null,
    bairroDestinoId: null
  };

 
  pontosColeta: PontoColeta[] = [];
  mostrarModalPontoColeta: boolean = false;
  pontoColetaParaEdicao: PontoColeta | null = null;
  pontoColetaParaCadastro: Partial<PontoColeta> & { bairroId: number | null } = {
    nome: '',
    responsavel: '',
    contato: '',
    endereco: '',
    tiposResiduos: [],
    bairroId: null
  };


  tiposResiduoDisponiveis = Object.values(TipoResiduo);

  constructor(
    private authService: AuthService,
    private caminhaoService: CaminhaoService,
    private rotaService: RotaService,
    private itinerarioService: ItinerarioService,
    private errorModalService: ErrorModalService,
    private bairroService: BairroService,
    private ruaService: RuaService,
    private pontoColetaService: PontoColetaService
  ) {}

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe(loggedIn => {
      this.isLoggedIn = loggedIn;
      if (loggedIn) {
        this.currentView = 'menu';
      } else {
        this.currentView = 'login';
      }
    });

    if (this.authService.getToken()) {
      this.isLoggedIn = true;
      this.currentView = 'menu';
    }

    this.errorSubscription = this.errorModalService.error$.subscribe(message => {
      this.errorMessage = message;
    });
  }

  ngOnDestroy(): void { if (this.errorSubscription) { this.errorSubscription.unsubscribe(); } }
  onLogin(): void { this.authService.login(this.username, this.password).subscribe(); }
  logout(): void { this.authService.logout(); }

  navigateTo(view: 'menu' | 'caminhoes' | 'itinerarios' | 'rotas' | 'areas'): void {
    this.currentView = view;
    this.currentAreaSubView = null;
    if (view === 'caminhoes') this.carregarCaminhoes();
    else if (view === 'rotas') this.carregarDadosParaRotas();
    else if (view === 'itinerarios') { this.carregarCaminhoes(); this.buscarItinerarios(); }
    else if (view === 'areas') this.navigateToAreaSubView('bairros');
  }

  closeErrorModal() { this.errorMessage = null; }


  carregarCaminhoes(): void { this.caminhaoService.listarCaminhoes().subscribe(data => this.caminhoes = data); }
  abrirModalNovoCaminhao(): void { this.caminhao = { placa: '', motorista: '', capacidade: 0, tiposResiduos: [] }; this.caminhaoParaEdicao = null; this.mostrarModalCaminhao = true; }
  abrirModalEdicaoCaminhao(caminhao: Caminhao): void { this.caminhao = { ...caminhao, tiposResiduos: [...caminhao.tiposResiduos] }; this.caminhaoParaEdicao = caminhao; this.mostrarModalCaminhao = true; }
  fecharModalCaminhao(): void { this.mostrarModalCaminhao = false; this.caminhaoParaEdicao = null; }
  salvarCaminhao(): void {
    const operacao = this.caminhaoParaEdicao
      ? this.caminhaoService.atualizarCaminhao(this.caminhao.id!, this.caminhao as Caminhao)
      : this.caminhaoService.salvarCaminhao(this.caminhao as Caminhao);
    operacao.subscribe({
      next: () => { alert('Operação realizada com sucesso!'); this.fecharModalCaminhao(); this.carregarCaminhoes(); },
      error: err => this.errorModalService.showError('Erro ao salvar caminhão: ' + (err.error?.error || err.message))
    });
  }
  excluirCaminhao(id: number | undefined): void { if (id !== undefined && confirm('Tem certeza?')) { this.caminhaoService.excluirCaminhao(id).subscribe({ next: () => { alert('Caminhão excluído!'); this.carregarCaminhoes(); }, error: err => this.errorModalService.showError('Erro: ' + (err.error?.error || err.message)) }); } }
  toggleTipoResiduo(tipo: TipoResiduo, event: Event): void { const isChecked = (event.target as HTMLInputElement).checked; const set = new Set(this.caminhao.tiposResiduos); isChecked ? set.add(tipo) : set.delete(tipo); this.caminhao.tiposResiduos = Array.from(set); }
  isTipoResiduoSelected(tipo: TipoResiduo): boolean { return this.caminhao.tiposResiduos?.includes(tipo) ?? false; }
  formatarTiposResiduos(tipos?: TipoResiduo[]): string { return tipos?.join(', ') || ''; }

  
  carregarDadosParaRotas(): void {
    this.pontoColetaService.listarPontosColeta().subscribe(data => this.pontosDeColetaParaRota = data);
    this.caminhaoService.listarCaminhoes().subscribe(data => this.caminhoes = data);
    this.rotaService.listarRotas().subscribe(data => this.rotas = data);
    this.resetarFormularioRota();
  }


  resetarFormularioRota(): void {
    this.rotaParaCalculo = { pontoOrigemId: null, pontoDestinoId: null, tipoResiduo: null, caminhaoId: null, data: '' };
    this.pontosDestinoFiltrados = [];
    this.residuosEmComum = [];
    this.caminhoesFiltrados = [];
  }


  onOrigemChange(): void {
    this.rotaParaCalculo.pontoDestinoId = null;
    this.rotaParaCalculo.tipoResiduo = null;
    this.rotaParaCalculo.caminhaoId = null;
    this.residuosEmComum = [];
    this.caminhoesFiltrados = [];

    const pontoOrigem = this.pontosDeColetaParaRota.find(p => p.id === this.rotaParaCalculo.pontoOrigemId);
    if (!pontoOrigem) {
      this.pontosDestinoFiltrados = [];
      return;
    }

    this.pontosDestinoFiltrados = this.pontosDeColetaParaRota.filter(pontoDestino => 
      pontoDestino.id !== pontoOrigem.id && 
      pontoDestino.tiposResiduos.some(residuo => pontoOrigem.tiposResiduos.includes(residuo))
    );
  }

  // NOVO: Chamado quando o Ponto de Destino é alterado
  onDestinoChange(): void {
    this.rotaParaCalculo.tipoResiduo = null;
    this.rotaParaCalculo.caminhaoId = null;
    this.caminhoesFiltrados = [];

    const pontoOrigem = this.pontosDeColetaParaRota.find(p => p.id === this.rotaParaCalculo.pontoOrigemId);
    const pontoDestino = this.pontosDeColetaParaRota.find(p => p.id === this.rotaParaCalculo.pontoDestinoId);
    if (!pontoOrigem || !pontoDestino) {
      this.residuosEmComum = [];
      return;
    }
    // Encontra apenas os resíduos que ambos os pontos (origem e destino) aceitam
    this.residuosEmComum = pontoOrigem.tiposResiduos.filter(residuo => pontoDestino.tiposResiduos.includes(residuo));
  }

  // NOVO: Chamado quando o Tipo de Resíduo é alterado
  onResiduoChange(): void {
    this.rotaParaCalculo.caminhaoId = null;
    const residuoSelecionado = this.rotaParaCalculo.tipoResiduo;
    if (!residuoSelecionado) {
      this.caminhoesFiltrados = [];
      return;
    }
    // Filtra os caminhões que podem transportar o resíduo selecionado
    this.caminhoesFiltrados = this.caminhoes.filter(caminhao => caminhao.tiposResiduos.includes(residuoSelecionado));
  }

  calcularRotaInteligente(): void {
    const { pontoOrigemId, pontoDestinoId, caminhaoId, tipoResiduo, data } = this.rotaParaCalculo;
    if (!pontoOrigemId || !pontoDestinoId || !caminhaoId || !tipoResiduo || !data) { this.errorModalService.showError("Todos os campos são obrigatórios."); return; }
    this.rotaService.calcularEAgendarRotaInteligente(pontoOrigemId, pontoDestinoId, caminhaoId, tipoResiduo, data)
      .subscribe({
        next: itinerarioAgendado => { alert(`Rota agendada com sucesso para o caminhão ${itinerarioAgendado.caminhao.placa}!`); this.carregarDadosParaRotas(); },
        error: err => this.errorModalService.showError('Erro ao calcular rota: ' + (err.error?.error || err.message))
      });
  }
  formatarBairros(bairros: Bairro[] | undefined): string { return bairros?.map(b => b.nome).join(' → ') || 'N/A'; }

  // --- Métodos de Itinerários (sem alterações) ---
  buscarItinerarios(): void {
    this.itinerarios = [];
    const { caminhaoId, mes, ano, motorista } = this.filtroItinerario;
    if (!caminhaoId && !mes && !ano && !motorista) { this.errorModalService.showError("Por favor, preencha ao menos um filtro."); return; }
    if ((mes && !ano) || (!mes && ano)) { this.errorModalService.showError("Para filtrar por data, preencha Mês e Ano."); return; }
    this.itinerarioService.listarItinerariosComFiltros(caminhaoId || 0, mes, ano, motorista)
      .subscribe({
        next: (data: Itinerario[]) => { this.itinerarios = data; if (data.length === 0) this.errorModalService.showError("Nenhum itinerário encontrado."); },
        error: (err) => this.errorModalService.showError('Erro: ' + (err.error?.error || err.message))
      });
  }
  limparFiltrosItinerario(): void { this.filtroItinerario = { caminhaoId: 0, mes: null, ano: null, motorista: '' }; this.itinerarios = []; }

  // --- Métodos de Áreas (sem alterações) ---
  navigateToAreaSubView(subView: 'bairros' | 'ruas' | 'pontosColeta'): void { this.currentAreaSubView = subView; this.carregarBairros(); if (subView === 'ruas') this.carregarRuas(); else if (subView === 'pontosColeta') this.carregarPontosColeta(); }
  carregarBairros(): void { this.bairroService.listarBairros().subscribe({ next: data => this.bairros = data }); }
  carregarRuas(): void { this.ruaService.listarRuas().subscribe({ next: data => this.ruas = data }); }
  carregarPontosColeta(): void { this.pontoColetaService.listarPontosColeta().subscribe({ next: data => this.pontosColeta = data }); }
  abrirModalNovoBairro(): void { this.bairroParaCadastro = { nome: '' }; this.mostrarModalBairro = true; }
  fecharModalBairro(): void { this.mostrarModalBairro = false; }
  salvarBairro(): void { this.bairroService.salvarBairro(this.bairroParaCadastro as Bairro).subscribe({ next: () => { alert('Bairro salvo!'); this.fecharModalBairro(); this.carregarBairros(); }, error: err => this.errorModalService.showError('Erro: ' + (err.error?.error || err.message)) }); }
  excluirBairro(id: number | undefined): void { if (id !== undefined && confirm('Tem certeza?')) { this.bairroService.excluirBairro(id).subscribe({ next: () => { alert('Bairro excluído!'); this.carregarBairros(); }, error: err => this.errorModalService.showError('Erro: ' + (err.error?.error || err.message)) }); } }
  abrirModalNovaRua(): void { this.ruaParaCadastro = { distancia: 0, bairroOrigemId: null, bairroDestinoId: null }; this.mostrarModalRua = true; if (this.bairros.length === 0) this.carregarBairros(); }
  fecharModalRua(): void { this.mostrarModalRua = false; }
  salvarRua(): void {
    if (!this.ruaParaCadastro.bairroOrigemId || !this.ruaParaCadastro.bairroDestinoId) { this.errorModalService.showError('Bairro de origem e destino são obrigatórios.'); return; }
    if (this.ruaParaCadastro.bairroOrigemId === this.ruaParaCadastro.bairroDestinoId) { this.errorModalService.showError('O bairro de origem e destino não podem ser iguais.'); return; }
    const ruaPayload = { id: this.ruaParaCadastro.id, distancia: this.ruaParaCadastro.distancia, bairroOrigem: { id: this.ruaParaCadastro.bairroOrigemId } as Bairro, bairroDestino: { id: this.ruaParaCadastro.bairroDestinoId } as Bairro };
    this.ruaService.salvarRua(ruaPayload).subscribe({ next: () => { alert('Rua salva!'); this.fecharModalRua(); this.carregarRuas(); }, error: err => this.errorModalService.showError('Erro: ' + (err.error?.error || err.message)) });
  }
  excluirRua(id: number | undefined): void { if (id !== undefined && confirm('Tem certeza?')) { this.ruaService.excluirRua(id).subscribe({ next: () => { alert('Rua excluída!'); this.carregarRuas(); }, error: err => this.errorModalService.showError('Erro: ' + (err.error?.error || err.message)) }); } }
  abrirModalNovoPontoColeta(): void { this.pontoColetaParaEdicao = null; this.pontoColetaParaCadastro = { nome: '', responsavel: '', contato: '', endereco: '', tiposResiduos: [], bairroId: null }; this.mostrarModalPontoColeta = true; if (this.bairros.length === 0) this.carregarBairros(); }
  abrirModalEdicaoPontoColeta(ponto: PontoColeta): void { this.pontoColetaParaEdicao = ponto; this.pontoColetaParaCadastro = { id: ponto.id, nome: ponto.nome, responsavel: ponto.responsavel, contato: ponto.contato, endereco: ponto.endereco, tiposResiduos: [...ponto.tiposResiduos], bairroId: ponto.bairro?.id || null }; this.mostrarModalPontoColeta = true; if (this.bairros.length === 0) this.carregarBairros(); }
  fecharModalPontoColeta(): void { this.mostrarModalPontoColeta = false; this.pontoColetaParaEdicao = null; }
  salvarPontoColeta(): void {
    if (!this.pontoColetaParaCadastro.bairroId) { this.errorModalService.showError('O bairro é obrigatório.'); return; }
    const payload = { id: this.pontoColetaParaCadastro.id, nome: this.pontoColetaParaCadastro.nome, responsavel: this.pontoColetaParaCadastro.responsavel, contato: this.pontoColetaParaCadastro.contato, endereco: this.pontoColetaParaCadastro.endereco, tiposResiduos: this.pontoColetaParaCadastro.tiposResiduos, bairro: { id: this.pontoColetaParaCadastro.bairroId } as Bairro };
    this.pontoColetaService.salvarPontoColeta(payload).subscribe({ next: () => { alert('Ponto salvo!'); this.fecharModalPontoColeta(); this.carregarPontosColeta(); }, error: err => this.errorModalService.showError('Erro: ' + (err.error?.error || err.message)) });
  }
  excluirPontoColeta(id: number | undefined): void { if (id !== undefined && confirm('Tem certeza?')) { this.pontoColetaService.excluirPontoColeta(id).subscribe({ next: () => { alert('Ponto excluído!'); this.carregarPontosColeta(); }, error: err => this.errorModalService.showError('Erro: ' + (err.error?.error || err.message)) }); } }
  togglePontoTipoResiduo(tipo: TipoResiduo, event: Event): void { const isChecked = (event.target as HTMLInputElement).checked; const set = new Set(this.pontoColetaParaCadastro.tiposResiduos); isChecked ? set.add(tipo) : set.delete(tipo); this.pontoColetaParaCadastro.tiposResiduos = Array.from(set); }
  isPontoTipoResiduoSelected(tipo: TipoResiduo): boolean { return this.pontoColetaParaCadastro.tiposResiduos?.includes(tipo) ?? false; }
}
