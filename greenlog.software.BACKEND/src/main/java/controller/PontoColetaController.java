package controller;

import entities.PontoColeta;
import jakarta.validation.Valid;
import services.PontoColetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/pontosColeta") 
public class PontoColetaController {

    private final PontoColetaService pontoColetaService;

    public PontoColetaController(PontoColetaService pontoColetaService) {
        this.pontoColetaService = pontoColetaService;
    }

    @GetMapping
    public List<PontoColeta> listar() {
        return pontoColetaService.findAll();
    }

    @PostMapping
    public PontoColeta criar(@Valid @RequestBody PontoColeta pontoColeta) {
        return pontoColetaService.save(pontoColeta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoColeta> atualizar(@PathVariable Long id, @Valid @RequestBody PontoColeta pontoColeta) {
        PontoColeta pontoAtualizado = pontoColetaService.update(id, pontoColeta);
        return ResponseEntity.ok(pontoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pontoColetaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
