package services;

import entities.Rua;
import repositories.RuaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RuaService {
    private final RuaRepository RuaRepository;

    public RuaService(RuaRepository RuaRepository) {
        this.RuaRepository = RuaRepository;
    }

    public List<Rua> findAll() {
        return RuaRepository.findAll();
    }

    public Optional<Rua> findById(Long id) {
        return RuaRepository.findById(id);
    }

    public Rua save(Rua rua) {
        return RuaRepository.save(rua);
    }

    public void delete(Long id) {
        RuaRepository.deleteById(id);
    }
}
