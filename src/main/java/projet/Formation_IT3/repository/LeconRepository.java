package  projet.Formation_IT3.repository;

import  projet.Formation_IT3.modeles.Lecon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour la gestion des leçons.
 */
@Repository
public interface LeconRepository extends JpaRepository<Lecon, Long> {

    /** Récupère toutes les leçons d'un module, triées par ordre croissant */
    List<Lecon> findByModuleIdOrderByOrdreAsc(Long moduleId);
}
