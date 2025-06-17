package services;

import entities.Caminhao;
import entities.TipoResiduo;
import repositories.CaminhaoRepository;
import repositories.ItinerarioRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CaminhaoService {


    private final CaminhaoRepository caminhaoRepository;
    private final ItinerarioRepository itinerarioRepository;


    public CaminhaoService(CaminhaoRepository caminhaoRepository, ItinerarioRepository itinerarioRepository) {
        this.caminhaoRepository = caminhaoRepository;
        this.itinerarioRepository = itinerarioRepository;
    }

    public List<Caminhao> findAll() {
        return caminhaoRepository.findAll();
    }

    public Optional<Caminhao> findById(Long id) {
        return caminhaoRepository.findById(id);
    }

    public Caminhao save(Caminhao caminhao) {
        return caminhaoRepository.save(caminhao);
    }

    public Caminhao update(Long id, Caminhao caminhaoComNovosDados) {
        // 1. Busca o caminhão que já existe no banco de dados.
        Caminhao caminhaoExistente = caminhaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caminhão com ID " + id + " não encontrado para atualização."));

        boolean temItinerarioFuturo = itinerarioRepository.existsByCaminhaoIdAndDataGreaterThanEqual(id, LocalDate.now());

        Set<TipoResiduo> tiposAntigos = caminhaoExistente.getTiposResiduos();
        Set<TipoResiduo> tiposNovos = caminhaoComNovosDados.getTiposResiduos();
        boolean mudouTiposResiduo = !tiposAntigos.equals(tiposNovos);


        if (temItinerarioFuturo && mudouTiposResiduo) {
            throw new IllegalArgumentException(
                "Não é possível alterar os tipos de resíduo de um caminhão que possui itinerários futuros agendados."
            );
        }


        caminhaoExistente.setPlaca(caminhaoComNovosDados.getPlaca());
        caminhaoExistente.setMotorista(caminhaoComNovosDados.getMotorista());
        caminhaoExistente.setCapacidade(caminhaoComNovosDados.getCapacidade());
        caminhaoExistente.setTiposResiduos(caminhaoComNovosDados.getTiposResiduos()); // Atualiza os tipos se a regra permitir

        return caminhaoRepository.save(caminhaoExistente);
    }

    public void delete(Long id) {
        if (!caminhaoRepository.existsById(id)) {
            throw new EntityNotFoundException("Caminhão com ID " + id + " não encontrado.");
        }


        if (itinerarioRepository.existsByCaminhaoIdAndDataGreaterThanEqual(id, LocalDate.now())) {
            throw new DataIntegrityViolationException("Não é possível excluir o caminhão, pois ele está associado a itinerários presentes ou futuros.");
        }

        caminhaoRepository.deleteById(id);
    
    }
}