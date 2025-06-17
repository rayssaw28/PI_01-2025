package repositories;

import entities.Caminhao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaminhaoRepository extends JpaRepository<Caminhao, Long> {
    void deleteCaminhaoById(Long id);
}
