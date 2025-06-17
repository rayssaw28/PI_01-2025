package controller;

import entities.Caminhao;
import repositories.CaminhaoRepository;
import services.CaminhaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/caminhoes")
public class CaminhaoController {


    private final CaminhaoService caminhaoService;

    public CaminhaoController(CaminhaoService caminhaoService) {
        this.caminhaoService = caminhaoService;
    }
    

    @GetMapping
    public List<Caminhao> listarTodos() {
        return caminhaoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Caminhao> buscarPorId(@PathVariable Long id) {
        return caminhaoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Caminhao criar(@Valid @RequestBody Caminhao caminhao) { 
        return caminhaoService.save(caminhao);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Caminhao> atualizar(@PathVariable Long id, @Valid @RequestBody Caminhao caminhao) { // <-- E AQUI
        Caminhao caminhaoAtualizado = caminhaoService.update(id, caminhao);
        return ResponseEntity.ok(caminhaoAtualizado);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        caminhaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}