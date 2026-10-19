package  projet.Formation_IT3.repository;

import  projet.Formation_IT3.modeles.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA pour la gestion des utilisateurs.
 * Spring Data génère automatiquement les requêtes SQL à partir des noms de méthodes.
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /** Recherche un utilisateur par son email (utilisé pour la connexion) */
    Optional<Utilisateur> findByEmail(String email);

    /** Recherche un utilisateur par son ID employé KAOLO */
    Optional<Utilisateur> findByIdEmploye(String idEmploye);

    /** Vérifie si un email est déjà utilisé */
    boolean existsByEmail(String email);

    /** Vérifie si un ID employé est déjà enregistré */
    boolean existsByIdEmploye(String idEmploye);
}
