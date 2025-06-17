package services;

import entities.Caminhao;
import entities.Itinerario;
import entities.Rota;
import jakarta.persistence.EntityNotFoundException;
import repositories.CaminhaoRepository;
import repositories.ItinerarioRepository;
import repositories.RotaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ItinerarioService {

    private final ItinerarioRepository itinerarioRepository;
    private final RotaRepository rotaRepository;
    private final CaminhaoRepository caminhaoRepository;

    public ItinerarioService(ItinerarioRepository itinerarioRepository, RotaRepository rotaRepository, CaminhaoRepository caminhaoRepository) {
        this.itinerarioRepository = itinerarioRepository;
        this.rotaRepository = rotaRepository;
        this.caminhaoRepository = caminhaoRepository;
    }

    public Itinerario criarItinerario(Long rotaId, LocalDate data) {
        Rota rota = rotaRepository.findById(rotaId)
            .orElseThrow(() -> new EntityNotFoundException("Rota com ID " + rotaId + " não encontrada."));

        Caminhao caminhaoDaRota = rota.getCaminhao();

        itinerarioRepository.findByCaminhaoAndData(caminhaoDaRota, data)
            .ifPresent(itinerarioExistente -> {
                throw new IllegalArgumentException(
                    "Agendamento falhou: O caminhão de placa " + caminhaoDaRota.getPlaca() +
                    " já possui um itinerário agendado para a data " + data
                );
            });

        Itinerario novoItinerario = new Itinerario();
        novoItinerario.setRota(rota);
        novoItinerario.setCaminhao(caminhaoDaRota);
        novoItinerario.setData(data);

        return itinerarioRepository.save(novoItinerario);
    }
    
    public List<Itinerario> listarItinerariosComFiltros(Long caminhaoId, Integer mes, Integer ano, String motorista) {
        LocalDate dataInicio = null;
        LocalDate dataFim = null;


        if ((mes != null && ano == null) || (mes == null && ano != null)) {
            throw new IllegalArgumentException("Para filtrar por data, é necessário fornecer tanto o mês quanto o ano.");
        }
        

        if (mes != null && ano != null) {
            if (mes < 1 || mes > 12) {
                throw new IllegalArgumentException("Mês inválido. Por favor, forneça um valor entre 1 e 12.");
            }
            YearMonth anoMes = YearMonth.of(ano, mes);
            dataInicio = anoMes.atDay(1);
            dataFim = anoMes.atEndOfMonth();
        }

       
        Long idCaminhaoFiltrar = (caminhaoId == null) ? 0L : caminhaoId;

        return itinerarioRepository.findWithFilters(idCaminhaoFiltrar, motorista, dataInicio, dataFim);
    }
}