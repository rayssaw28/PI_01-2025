package repositories;



import entities.PontoColeta;
import entities.TipoResiduo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface PontoColetaRepository extends JpaRepository<PontoColeta, Long> {
    
    List<PontoColeta> findByBairroId(Long bairroId);

    @Query("SELECT p FROM PontoColeta p JOIN p.tiposResiduos t WHERE t = :tipoResiduo")
    List<PontoColeta> findByTipoResiduo(@Param("tipoResiduo") TipoResiduo tipoResiduo);
    
    boolean existsByBairroId(Long bairroId);
}