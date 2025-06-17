package repositories;

import entities.Caminhao;
import entities.Itinerario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItinerarioRepository extends JpaRepository<Itinerario, Long> {
    

    Optional<Itinerario> findByCaminhaoAndData(Caminhao caminhao, LocalDate data);
    boolean existsByCaminhaoId(Long caminhaoId);
    boolean existsByCaminhaoIdAndDataGreaterThanEqual(Long caminhaoId, LocalDate data);


    @Query("SELECT i FROM Itinerario i JOIN i.caminhao c WHERE " +
           "(:caminhaoId = 0L OR c.id = :caminhaoId) AND " +
           "(:motorista IS NULL OR :motorista = '' OR LOWER(c.motorista) LIKE LOWER(CONCAT('%', :motorista, '%'))) AND " +
           "i.data >= COALESCE(:dataInicio, CAST('1900-01-01' AS java.time.LocalDate)) AND " +
           "i.data <= COALESCE(:dataFim, CAST('9999-12-31' AS java.time.LocalDate))")
    List<Itinerario> findWithFilters(
        @Param("caminhaoId") Long caminhaoId,
        @Param("motorista") String motorista,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );
}
