package  projet.Formation_IT3.repository;

import  projet.Formation_IT3.enums.NiveauEnum;
import projet.Formation_IT3.modeles.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour la gestion des formations.
 */
@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {

    /** Récupère toutes les formations publiées (visibles dans le catalogue) */
    List<Formation> findByPublieeTrue();

    /** Filtre les formations par domaine technique */
    List<Formation> findByDomaineAndPublieeTrue(String domaine);

    /** Filtre les formations par niveau de difficulté */
    List<Formation> findByNiveauAndPublieeTrue(NiveauEnum niveau);

    /** Recherche de formations par mot-clé dans le titre */
    List<Formation> findByTitreContainingIgnoreCaseAndPublieeTrue(String motCle);

    /** Récupère les formations créées par un formateur donné */
    List<Formation> findByFormateurId(Long formateurId);
}
