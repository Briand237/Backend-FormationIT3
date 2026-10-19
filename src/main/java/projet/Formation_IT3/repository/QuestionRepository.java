package projet.Formation_IT3.repository;

import projet.Formation_IT3.enums.TypeEvalEnum;
import projet.Formation_IT3.modeles.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour la gestion des questions de QCM.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /** Questions d'un module précis (quiz de fin de module), triées par ordre */
    List<Question> findByModuleIdOrderByOrdreAsc(Long moduleId);

    /** Questions de l'examen final d'une formation (type EXAMEN_FINAL, sans module) */
    List<Question> findByFormationIdAndTypeOrderByOrdreAsc(Long formationId, TypeEvalEnum type);

    /** Toutes les questions d'une formation, tous types confondus */
    List<Question> findByFormationId(Long formationId);

    /** Compte le nombre de questions pour un module (utile pour savoir si un quiz existe) */
    long countByModuleId(Long moduleId);

    /** Compte le nombre de questions d'examen final pour une formation */
    long countByFormationIdAndType(Long formationId, TypeEvalEnum type);
}
