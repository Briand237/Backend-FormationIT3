package  projet.Formation_IT3.repository;

import  projet.Formation_IT3.enums.StatutEnum;
import  projet.Formation_IT3.modeles.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour la gestion des inscriptions.
 */
@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    /** Toutes les inscriptions d'un utilisateur */
    List<Inscription> findByUtilisateurId(Long utilisateurId);

    /** Toutes les inscriptions à une formation donnée */
    List<Inscription> findByFormationId(Long formationId);

    /** Recherche une inscription spécifique utilisateur + formation */
    Optional<Inscription> findByUtilisateurIdAndFormationId(Long utilisateurId, Long formationId);

    /** Vérifie si un utilisateur est déjà inscrit à une formation */
    boolean existsByUtilisateurIdAndFormationId(Long utilisateurId, Long formationId);

    /** Récupère les inscriptions filtrées par statut */
    List<Inscription> findByUtilisateurIdAndStatut(Long utilisateurId, StatutEnum statut);
}
