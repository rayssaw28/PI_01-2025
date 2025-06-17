package controller;

import entities.Bairro;
import services.BairroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bairro")
public class BairroController {

    @Autowired
    private BairroService BairroService;

    @GetMapping
    public List<Bairro> listar() {
        return BairroService.findAll();
    }
    
    @GetMapping("/")
    public String home() {
        return "🚀 Aplicação está rodando com sucesso!";
    }

       
    @GetMapping("/{id}")
    public Optional<Bairro> buscarPorId(@PathVariable Long id) {
        return BairroService.findById(id);
    }

    @PostMapping
    public Bairro criar(@RequestBody Bairro bairro) {
        return BairroService.save(bairro);
    }

    @PutMapping("/{id}")
    public Bairro atualizar(@RequestBody Bairro bairro) {
        return BairroService.save(bairro);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        BairroService.delete(id);
    }
}