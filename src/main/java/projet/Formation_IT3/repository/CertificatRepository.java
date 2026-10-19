package  projet.Formation_IT3.repository;

import  projet.Formation_IT3.modeles.Certificat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour la gestion des certificats délivrés.
 */
@Repository
public interface CertificatRepository extends JpaRepository<Certificat, Long> {

    /** Tous les certificats d'un utilisateur */
    List<Certificat> findByUtilisateurId(Long utilisateurId);

    /** Recherche un certificat par son identifiant unique public (KA-XXXX) */
    Optional<Certificat> findByIdentifiantUnique(String identifiantUnique);

    /** Vérifie si un certificat existe pour un utilisateur et une formation */
    boolean existsByUtilisateurIdAndFormationId(Long utilisateurId, Long formationId);

    /** Récupère le certificat d'un utilisateur pour une formation spécifique */
    Optional<Certificat> findByUtilisateurIdAndFormationId(Long utilisateurId, Long formationId);
}
