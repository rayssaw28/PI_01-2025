package controller;

import entities.*;
import jakarta.persistence.EntityNotFoundException;
import repositories.CaminhaoRepository;
import repositories.PontoColetaRepository; // Importar
import services.RotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/rotas") 
public class RotaController {

    @Autowired
    private RotaService rotaService;

    @Autowired
    private PontoColetaRepository pontoColetaRepository; 

    @Autowired
    private CaminhaoRepository caminhaoRepository;

    @PostMapping("/calcular-inteligente")
    public ResponseEntity<Itinerario> calcularEAgendarRota(
            @RequestParam Long pontoOrigemId,    
            @RequestParam Long pontoDestinoId,  
            @RequestParam Long caminhaoId,
            @RequestParam TipoResiduo tipoResiduo,
            @RequestParam LocalDate data) {


        PontoColeta origem = pontoColetaRepository.findById(pontoOrigemId)
            .orElseThrow(() -> new EntityNotFoundException("Ponto de coleta de origem com ID " + pontoOrigemId + " não encontrado."));
        
        PontoColeta destino = pontoColetaRepository.findById(pontoDestinoId)
            .orElseThrow(() -> new EntityNotFoundException("Ponto de coleta de destino com ID " + pontoDestinoId + " não encontrado."));

        Caminhao caminhao = caminhaoRepository.findById(caminhaoId)
            .orElseThrow(() -> new EntityNotFoundException("Caminhão com ID " + caminhaoId + " não encontrado."));

        Itinerario itinerarioCriado = rotaService.calcularERotarInteligentemente(
            origem,
            destino,
            caminhao,
            tipoResiduo,
            data
        );
        
        return new ResponseEntity<>(itinerarioCriado, HttpStatus.CREATED);
    }
    
    @GetMapping
    public List<Rota> listar() {
        return rotaService.findAll();
    }
}
