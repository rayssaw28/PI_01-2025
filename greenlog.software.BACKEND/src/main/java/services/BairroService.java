package services;


import entities.Bairro;
import jakarta.persistence.EntityNotFoundException;
import repositories.BairroRepository;
import repositories.PontoColetaRepository;
import repositories.RuaRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BairroService {
    private final BairroRepository bairroRepository;
    private final RuaRepository ruaRepository;
    private final PontoColetaRepository pontoColetaRepository;

    public BairroService(BairroRepository bairroRepository, RuaRepository ruaRepository, PontoColetaRepository pontoColetaRepository) {
        this.bairroRepository = bairroRepository;
        this.ruaRepository = ruaRepository;
        this.pontoColetaRepository = pontoColetaRepository;
    }

    public void delete(Long id) {
        if (!bairroRepository.existsById(id)) {
            throw new EntityNotFoundException("Bairro com ID " + id + " não encontrado.");
        }

        if (ruaRepository.existsByBairroOrigemIdOrBairroDestinoId(id, id)) {
            throw new DataIntegrityViolationException("Não é possível excluir o bairro, pois ele está conectado a uma ou mais ruas.");
        }

        if (pontoColetaRepository.existsByBairroId(id)) {
            throw new DataIntegrityViolationException("Não é possível excluir o bairro, pois ele contém um ou mais pontos de coleta.");
        }

        bairroRepository.deleteById(id);
    }

    public List<Bairro> findAll() {
        return bairroRepository.findAll();
    }

    public Optional<Bairro> findById(Long id) {
        return bairroRepository.findById(id);
    }

    public Bairro save(Bairro bairro) {
        return bairroRepository.save(bairro);
    }

}