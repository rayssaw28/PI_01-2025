package controller;

import entities.Itinerario;
import services.ItinerarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/itinerarios")
public class ItinerarioController {

    private final ItinerarioService itinerarioService;

    public ItinerarioController(ItinerarioService itinerarioService) {
        this.itinerarioService = itinerarioService;
    }

    @GetMapping
    public ResponseEntity<List<Itinerario>> listarComFiltros(
            @RequestParam(required = false) Long caminhaoId,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String motorista
    ) {
        List<Itinerario> itinerarios = itinerarioService.listarItinerariosComFiltros(caminhaoId, mes, ano, motorista);
        return ResponseEntity.ok(itinerarios);
    }

    @PostMapping("/agendar")
    public ResponseEntity<Itinerario> agendarItinerario(
            @RequestParam Long rotaId,
            @RequestParam LocalDate data) {
        Itinerario itinerarioCriado = itinerarioService.criarItinerario(rotaId, data);
        return new ResponseEntity<>(itinerarioCriado, HttpStatus.CREATED);
    }
}
