package services;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import entities.*;
import jakarta.persistence.EntityNotFoundException;
import repositories.*;

@Service
public class RotaService {
    
    private final RotaRepository rotaRepository;
    private final BairroRepository bairroRepository;
    private final RuaRepository ruaRepository;
    private final ItinerarioRepository itinerarioRepository;
    private final PontoColetaRepository pontoColetaRepository;

    public RotaService(RotaRepository rotaRepository, BairroRepository bairroRepository, 
                       RuaRepository ruaRepository, ItinerarioRepository itinerarioRepository,
                       PontoColetaRepository pontoColetaRepository) {
        this.rotaRepository = rotaRepository;
        this.bairroRepository = bairroRepository;
        this.ruaRepository = ruaRepository;
        this.itinerarioRepository = itinerarioRepository;
        this.pontoColetaRepository = pontoColetaRepository;
    }

    public List<Rota> findAll() { return rotaRepository.findAll(); }
    public Optional<Rota> findById(Long id) { return rotaRepository.findById(id); }
    public void delete(Long id) { rotaRepository.deleteById(id); }

    @Transactional
    public Itinerario calcularERotarInteligentemente(
            PontoColeta pontoOrigem, PontoColeta pontoDestino, Caminhao caminhao, 
            TipoResiduo tipoResiduo, LocalDate data) {


        if (!caminhao.getTiposResiduos().contains(tipoResiduo)) {
            throw new IllegalArgumentException("O caminhão selecionado não está autorizado a transportar " + tipoResiduo + ".");
        }
        itinerarioRepository.findByCaminhaoAndData(caminhao, data)
            .ifPresent(it -> {
                throw new IllegalArgumentException("Este caminhão já possui um itinerário agendado para a data " + data + ".");
            });

        // --- Lógica do Grafo (COM LOGS DE DEPURAÇÃO) ---

        // 1. Montar o grafo base
        Graph<Bairro, Rua> grafo = new SimpleWeightedGraph<>(Rua.class);
        System.out.println("\n--- INICIANDO CONSTRUÇÃO DO GRAFO ---");
        List<Bairro> todosBairros = bairroRepository.findAll();
        todosBairros.forEach(b -> {
            grafo.addVertex(b);
            System.out.println("Adicionado Vértice: " + b);
        });
    
        System.out.println("\n--- ADICIONANDO ARESTAS (RUAS) ---");
        List<Rua> todasRuas = ruaRepository.findAll();
        for (Rua rua : todasRuas) {
            Bairro origem = rua.getBairroOrigem();
            Bairro destino = rua.getBairroDestino();
            System.out.println("Processando Aresta: " + rua);
    
            if (grafo.containsVertex(origem) && grafo.containsVertex(destino)) {
                boolean adicionado = grafo.addEdge(origem, destino, rua);
                System.out.println("  > Vértices OK. Adicionando aresta... Sucesso: " + adicionado);
                if (adicionado) {
                    grafo.setEdgeWeight(rua, rua.getDistancia());
                }
            } else {
                System.out.println("  > ERRO: Vértice de origem ou destino não encontrado no grafo! Origem no grafo? " 
                    + grafo.containsVertex(origem) + ". Destino no grafo? " + grafo.containsVertex(destino));
            }
        }
        System.out.println("--- CONSTRUÇÃO DO GRAFO FINALIZADA ---\n");
        
      
        Bairro bairroOrigem = pontoOrigem.getBairro();
        Bairro bairroDestino = pontoDestino.getBairro();

        if (!grafo.containsVertex(bairroOrigem) || !grafo.containsVertex(bairroDestino)) {
            throw new RuntimeException("O bairro de origem ou destino não pôde ser encontrado no mapa de rotas.");
        }

        // 2. Identificar bairros de interesse
        Set<Long> bairrosDeInteresseIds = pontoColetaRepository.findByTipoResiduo(tipoResiduo)
                .stream()
                .map(ponto -> ponto.getBairro().getId())
                .collect(Collectors.toSet());
        bairrosDeInteresseIds.add(bairroOrigem.getId());
        bairrosDeInteresseIds.add(bairroDestino.getId());

        // 3. Aplicar penalidade
        final double PENALTY_FACTOR = 10.0;
        for (Rua rua : grafo.edgeSet()) {
            Bairro origemDaRua = grafo.getEdgeSource(rua);
            Bairro destinoDaRua = grafo.getEdgeTarget(rua);
            if (!bairrosDeInteresseIds.contains(origemDaRua.getId()) && !bairrosDeInteresseIds.contains(destinoDaRua.getId())) {
                grafo.setEdgeWeight(rua, rua.getDistancia() * PENALTY_FACTOR);
            }
        }

        // 4. Executar Dijkstra
        DijkstraShortestPath<Bairro, Rua> dijkstra = new DijkstraShortestPath<>(grafo);
        GraphPath<Bairro, Rua> caminhoMaisEficiente = dijkstra.getPath(bairroOrigem, bairroDestino);

        if (caminhoMaisEficiente == null) {
            // Este erro agora é mais específico: o problema é a conectividade do grafo.
            throw new RuntimeException("Não foi possível encontrar um caminho conectando os pontos de coleta selecionados. Verifique se os bairros estão conectados por ruas no mapa.");
        }

        // --- Criação e Persistência ---
        Rota rota = new Rota();
        rota.setCaminhao(caminhao);
        rota.setBairros(caminhoMaisEficiente.getVertexList());
        rota.setRuas(caminhoMaisEficiente.getEdgeList());
        // Calcula a distância real, sem as penalidades, para salvar no banco.
        double distanciaReal = caminhoMaisEficiente.getEdgeList().stream().mapToDouble(Rua::getDistancia).sum();
        rota.setDistanciaTotal(distanciaReal);
        rota.setTipoResiduo(tipoResiduo);
        Rota rotaSalva = rotaRepository.save(rota);

        Itinerario novoItinerario = new Itinerario();
        novoItinerario.setData(data);
        novoItinerario.setCaminhao(caminhao);
        novoItinerario.setRota(rotaSalva);

        return itinerarioRepository.save(novoItinerario);
    }
}