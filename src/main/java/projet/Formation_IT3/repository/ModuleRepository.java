package  projet.Formation_IT3.repository;

import  projet.Formation_IT3.modeles.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour la gestion des modules d'une formation.
 */
@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    /** Récupère tous les modules d'une formation, triés par ordre croissant */
    List<Module> findByFormationIdOrderByOrdreAsc(Long formationId);

    /** Supprime tous les modules d'une formation */
    void deleteByFormationId(Long formationId);
}
