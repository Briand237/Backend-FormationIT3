package projet.Formation_IT3.repository;

import projet.Formation_IT3.modeles.ReponseEmploye;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour la traçabilité des réponses données par les employés.
 */
@Repository
public interface ReponseEmployeRepository extends JpaRepository<ReponseEmploye, Long> {

    /** Toutes les réponses données lors d'une évaluation (tentative) précise */
    List<ReponseEmploye> findByEvaluationId(Long evaluationId);
}
