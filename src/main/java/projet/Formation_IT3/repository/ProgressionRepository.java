package  projet.Formation_IT3.repository;

import  projet.Formation_IT3.modeles.Progression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour le suivi de la progression des apprenants.
 */
@Repository
public interface ProgressionRepository extends JpaRepository<Progression, Long> {

    /** Progression d'un utilisateur pour une leçon spécifique */
    Optional<Progression> findByUtilisateurIdAndLeconId(Long utilisateurId, Long leconId);

    /** Toutes les progressions d'un utilisateur */
    List<Progression> findByUtilisateurId(Long utilisateurId);

    /** Compte le nombre de leçons complétées par un utilisateur dans une formation */
    @Query("SELECT COUNT(p) FROM Progression p " +
           "WHERE p.utilisateur.id = :userId " +
           "AND p.lecon.module.formation.id = :formationId " +
           "AND p.estComplete = true")
    Long countLeconsCompletees(@Param("userId") Long userId,
                               @Param("formationId") Long formationId);
}
