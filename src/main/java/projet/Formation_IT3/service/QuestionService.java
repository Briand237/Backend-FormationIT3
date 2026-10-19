package projet.Formation_IT3.service;

import projet.Formation_IT3.dtos.PropositionRequestDto;
import projet.Formation_IT3.dtos.QuestionRequestDto;
import projet.Formation_IT3.enums.TypeEvalEnum;
import projet.Formation_IT3.modeles.Formation;
import projet.Formation_IT3.modeles.Module;
import projet.Formation_IT3.modeles.Proposition;
import projet.Formation_IT3.modeles.Question;
import projet.Formation_IT3.repository.FormationRepository;
import projet.Formation_IT3.repository.ModuleRepository;
import projet.Formation_IT3.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service gérant la création et l'administration des questions de QCM.
 * Utilisé exclusivement par les formateurs/admins via l'interface de gestion.
 */
@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private FormationRepository formationRepo;

    @Autowired
    private ModuleRepository moduleRepo;

    /**
     * Crée une nouvelle question avec ses propositions de réponse.
     * Valide qu'exactement une proposition est marquée comme correcte.
     */
    public Question creer(QuestionRequestDto dto) {

        // Validation métier : exactement une bonne réponse parmi les propositions
        long nbCorrectes = dto.getPropositions().stream()
            .filter(PropositionRequestDto::isEstCorrecte)
            .count();
        if (nbCorrectes != 1) {
            throw new RuntimeException(
                "Une question doit avoir exactement une seule proposition correcte (trouvé : " + nbCorrectes + ")"
            );
        }

        Formation formation = formationRepo.findById(dto.getFormationId())
            .orElseThrow(() -> new RuntimeException("Formation introuvable : " + dto.getFormationId()));

        Question question = new Question();
        question.setEnonce(dto.getEnonce());
        question.setType(dto.getType());
        question.setFormation(formation);
        question.setOrdre(dto.getOrdre());

        // Le module n'est renseigné que pour les questions de quiz de module
        if (dto.getType() == TypeEvalEnum.QUIZ && dto.getModuleId() != null) {
            Module module = moduleRepo.findById(dto.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module introuvable : " + dto.getModuleId()));
            question.setModule(module);
        }

        // Construction des propositions, rattachées à cette question
        List<Proposition> propositions = dto.getPropositions().stream()
            .map(p -> {
                Proposition prop = new Proposition();
                prop.setTexte(p.getTexte());
                prop.setEstCorrecte(p.isEstCorrecte());
                prop.setQuestion(question);
                return prop;
            })
            .collect(Collectors.toList());

        question.setPropositions(propositions);

        return questionRepo.save(question);
    }

    /** Récupère toutes les questions d'un module (vue formateur, avec bonnes réponses visibles) */
    public List<Question> getParModule(Long moduleId) {
        return questionRepo.findByModuleIdOrderByOrdreAsc(moduleId);
    }

    /** Récupère les questions de l'examen final d'une formation (vue formateur) */
    public List<Question> getExamenFinal(Long formationId) {
        return questionRepo.findByFormationIdAndTypeOrderByOrdreAsc(formationId, TypeEvalEnum.EXAMEN_FINAL);
    }

    public Question getParId(Long id) {
        return questionRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Question introuvable : " + id));
    }

    /** Modifie une question existante et remplace entièrement ses propositions */
    public Question modifier(Long id, QuestionRequestDto dto) {
        long nbCorrectes = dto.getPropositions().stream()
            .filter(PropositionRequestDto::isEstCorrecte)
            .count();
        if (nbCorrectes != 1) {
            throw new RuntimeException(
                "Une question doit avoir exactement une seule proposition correcte (trouvé : " + nbCorrectes + ")"
            );
        }

        Question question = getParId(id);
        question.setEnonce(dto.getEnonce());
        question.setOrdre(dto.getOrdre());

        // On vide les anciennes propositions et on les remplace
        // (orphanRemoval=true sur la relation supprimera les anciennes en base)
        question.getPropositions().clear();
        List<Proposition> nouvelles = dto.getPropositions().stream()
            .map(p -> {
                Proposition prop = new Proposition();
                prop.setTexte(p.getTexte());
                prop.setEstCorrecte(p.isEstCorrecte());
                prop.setQuestion(question);
                return prop;
            })
            .collect(Collectors.toList());
        question.getPropositions().addAll(nouvelles);

        return questionRepo.save(question);
    }

    public void supprimer(Long id) {
        questionRepo.delete(getParId(id));
    }
}
