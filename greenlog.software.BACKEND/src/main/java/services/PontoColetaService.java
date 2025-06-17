package services;

import entities.PontoColeta;
import jakarta.persistence.EntityNotFoundException;
import repositories.PontoColetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PontoColetaService {
    
    private final PontoColetaRepository pontoColetaRepository;

    public PontoColetaService(PontoColetaRepository pontoColetaRepository) {
        this.pontoColetaRepository = pontoColetaRepository;
    }

    public List<PontoColeta> findAll() {
        return pontoColetaRepository.findAll();
    }
    
    public PontoColeta save(PontoColeta pontoColeta) {
        return pontoColetaRepository.save(pontoColeta);
        
    }

    @Transactional
    public PontoColeta update(Long id, PontoColeta dadosParaAtualizar) {
        PontoColeta pontoExistente = pontoColetaRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ponto de Coleta com ID " + id + " não encontrado para atualização."));

        pontoExistente.setNome(dadosParaAtualizar.getNome());
        pontoExistente.setResponsavel(dadosParaAtualizar.getResponsavel());
        pontoExistente.setContato(dadosParaAtualizar.getContato());
        pontoExistente.setEndereco(dadosParaAtualizar.getEndereco());
        pontoExistente.setBairro(dadosParaAtualizar.getBairro());


        pontoExistente.getTiposResiduos().clear();

        pontoExistente.getTiposResiduos().addAll(dadosParaAtualizar.getTiposResiduos());


        return pontoColetaRepository.save(pontoExistente);
    }

    public void delete(Long id) {
        if (!pontoColetaRepository.existsById(id)) {
            throw new EntityNotFoundException("Não foi possível excluir. Ponto de Coleta com ID " + id + " não encontrado.");
        }
        pontoColetaRepository.deleteById(id);
    }
}
