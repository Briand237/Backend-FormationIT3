package  projet.Formation_IT3.repository;

import  projet.Formation_IT3.modeles.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour la gestion des évaluations et résultats d'examens.
 */
@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    /** Toutes les évaluations d'un utilisateur */
    List<Evaluation> findByUtilisateurId(Long utilisateurId);

    /** Évaluations d'un utilisateur pour une formation donnée */
    List<Evaluation> findByUtilisateurIdAndFormationId(Long utilisateurId, Long formationId);

    /** Nombre de tentatives pour un utilisateur et une formation */
    int countByUtilisateurIdAndFormationId(Long utilisateurId, Long formationId);
}
