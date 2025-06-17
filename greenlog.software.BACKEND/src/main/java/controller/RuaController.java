package controller;

import entities.Rua;
import services.RuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rua")
public class RuaController {

    @Autowired
    private RuaService RuaService;

    @GetMapping
    public List<Rua> listar() {
        return RuaService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Rua> buscarPorId(@PathVariable Long id) {
        return RuaService.findById(id);
    }

    @PostMapping
    public Rua criar(@RequestBody Rua rua) {
        return RuaService.save(rua);
    }

    @PutMapping("/{id}")
    public Rua atualizar(@RequestBody Rua rua) {
        return RuaService.save(rua);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        RuaService.delete(id);
    }
}
