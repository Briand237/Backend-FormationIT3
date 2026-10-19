package projet.Formation_IT3.repository;

import projet.Formation_IT3.modeles.Proposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour les propositions de réponse.
 */
@Repository
public interface PropositionRepository extends JpaRepository<Proposition, Long> {
}
