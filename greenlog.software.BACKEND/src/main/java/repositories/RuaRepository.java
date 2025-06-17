package repositories;



import entities.Bairro;
import entities.Rua;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RuaRepository extends JpaRepository<Rua, Long> {

    @Query("SELECT r FROM Rua r WHERE (r.bairroOrigem = :bairro1 AND r.bairroDestino = :bairro2) OR (r.bairroOrigem = :bairro2 AND r.bairroDestino = :bairro1)")
    Optional<Rua> findRuaByBairros(@Param("bairro1") Bairro bairro1, @Param("bairro2") Bairro bairro2);
    
    boolean existsByBairroOrigemIdOrBairroDestinoId(Long bairroOrigemId, Long bairroDestinoId);
}