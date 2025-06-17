package repositories;

import entities.Rota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RotaRepository extends JpaRepository<Rota, Long> {
    void deleteRotaById(Long id);
}
