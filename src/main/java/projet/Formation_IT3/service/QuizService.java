package projet.Formation_IT3.service;

import projet.Formation_IT3.dtos.QuestionEmployeDto;
import projet.Formation_IT3.dtos.ResultatQuizDto;
import projet.Formation_IT3.dtos.SoumissionQuizRequestDto;
import projet.Formation_IT3.enums.ResultatEnum;
import projet.Formation_IT3.enums.TypeEvalEnum;
import projet.Formation_IT3.modeles.*;
import projet.Formation_IT3.repository.*;
import projet.Formation_IT3.util.CertificatIdGenerateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service central du système de quiz : récupération sécurisée des questions
 * pour l'employé (sans les bonnes réponses), correction automatique côté
 * serveur, calcul du score, et génération du certificat si examen final réussi.
 *
 * C'est ici que se trouve la garantie de fiabilité de l'évaluation :
 * AUCUN score n'est jamais accepté venant directement du frontend — il est
 * systématiquement recalculé ici à partir des vraies réponses en base.
 */
@Service
public class QuizService {

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private PropositionRepository propositionRepo;

    @Autowired
    private ReponseEmployeRepository reponseRepo;

    @Autowired
    private EvaluationRepository evaluationRepo;

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private FormationRepository formationRepo;

    @Autowired
    private CertificatRepository certificatRepo;

    @Autowired
    private CertificatIdGenerateur idGenerateur;

    /**
     * Récupère les questions d'un quiz de module, formatées pour l'employé
     * (SANS les bonnes réponses — voir QuestionEmployeDto).
     */
    public List<QuestionEmployeDto> getQuestionsQuizModule(Long moduleId) {
        List<Question> questions = questionRepo.findByModuleIdOrderByOrdreAsc(moduleId);
        return convertirPourEmploye(questions);
    }

    /**
     * Récupère les questions de l'examen final d'une formation, formatées
     * pour l'employé (SANS les bonnes réponses).
     */
    public List<QuestionEmployeDto> getQuestionsExamenFinal(Long formationId) {
        List<Question> questions = questionRepo.findByFormationIdAndTypeOrderByOrdreAsc(
            formationId, TypeEvalEnum.EXAMEN_FINAL
        );
        return convertirPourEmploye(questions);
    }

    /** Convertit les entités Question en DTOs sécurisés (sans estCorrecte) */
    private List<QuestionEmployeDto> convertirPourEmploye(List<Question> questions) {
        return questions.stream().map(q -> {
            List<QuestionEmployeDto.PropositionEmployeDto> props = q.getPropositions().stream()
                .map(p -> new QuestionEmployeDto.PropositionEmployeDto(p.getId(), p.getTexte()))
                .collect(Collectors.toList());
            return new QuestionEmployeDto(q.getId(), q.getEnonce(), q.getOrdre(), props);
        }).collect(Collectors.toList());
    }

    /**
     * Corrige automatiquement un quiz/examen soumis par un employé.
     *
     * Étapes :
     * 1. Récupère les vraies questions et bonnes réponses depuis la base
     * 2. Compare avec les réponses envoyées par l'employé
     * 3. Calcule le score réel (jamais celui envoyé par le client)
     * 4. Enregistre une Evaluation + le détail de chaque ReponseEmploye
     * 5. Si EXAMEN_FINAL réussi, génère automatiquement le certificat
     *
     * @param utilisateurId ID de l'employé qui soumet
     * @param dto Les réponses choisies (question_id -> proposition_id)
     * @return Le résultat détaillé avec score et correction question par question
     */
    public ResultatQuizDto corriger(Long utilisateurId, SoumissionQuizRequestDto dto) {

        Utilisateur utilisateur = utilisateurRepo.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Formation formation = formationRepo.findById(dto.getFormationId())
            .orElseThrow(() -> new RuntimeException("Formation introuvable"));

        // Récupération des VRAIES questions depuis la base (jamais celles du frontend)
        List<Question> questions;
        if (dto.getType() == TypeEvalEnum.QUIZ && dto.getModuleId() != null) {
            questions = questionRepo.findByModuleIdOrderByOrdreAsc(dto.getModuleId());
        } else {
            questions = questionRepo.findByFormationIdAndTypeOrderByOrdreAsc(
                dto.getFormationId(), TypeEvalEnum.EXAMEN_FINAL
            );
        }

        if (questions.isEmpty()) {
            throw new RuntimeException("Aucune question disponible pour cette évaluation");
        }

        // Création de l'enregistrement Evaluation (avant correction, score calculé juste après)
        int tentative = evaluationRepo.countByUtilisateurIdAndFormationId(utilisateurId, dto.getFormationId()) + 1;

        Evaluation evaluation = new Evaluation();
        evaluation.setTitre(dto.getType() == TypeEvalEnum.EXAMEN_FINAL
            ? "Examen Final - " + formation.getTitre()
            : "Quiz de module - " + formation.getTitre());
        evaluation.setType(dto.getType());
        evaluation.setUtilisateur(utilisateur);
        evaluation.setFormation(formation);
        evaluation.setTentativeNum(tentative);
        evaluation.setStatut(ResultatEnum.EN_ATTENTE); // temporaire, mis à jour juste après
        evaluation = evaluationRepo.save(evaluation);

        // ===== CORRECTION AUTOMATIQUE =====
        List<ResultatQuizDto.DetailReponseDto> details = new ArrayList<>();
        int nbCorrectes = 0;

        for (Question question : questions) {
            Long propositionChoisieId = dto.getReponses().get(question.getId());

            // Recherche de la proposition correcte parmi les vraies propositions en base
            Proposition bonneProposition = question.getPropositions().stream()
                .filter(Proposition::isEstCorrecte)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                    "Question mal configurée (aucune bonne réponse) : " + question.getId()
                ));

            // Si l'employé n'a pas répondu à cette question, on considère comme incorrect
            Proposition propositionChoisie = null;
            boolean correcte = false;

            if (propositionChoisieId != null) {
                propositionChoisie = propositionRepo.findById(propositionChoisieId)
                    .orElseThrow(() -> new RuntimeException("Proposition introuvable : " + propositionChoisieId));
                correcte = propositionChoisie.getId().equals(bonneProposition.getId());
                if (correcte) nbCorrectes++;
            }

            // Sauvegarde de la trace de réponse (pour consultation ultérieure)
            if (propositionChoisie != null) {
                ReponseEmploye reponse = new ReponseEmploye();
                reponse.setEvaluation(evaluation);
                reponse.setQuestion(question);
                reponse.setPropositionChoisie(propositionChoisie);
                reponse.setCorrecte(correcte);
                reponseRepo.save(reponse);
            }

            details.add(new ResultatQuizDto.DetailReponseDto(
                question.getId(),
                question.getEnonce(),
                propositionChoisie != null ? propositionChoisie.getTexte() : "(Aucune réponse)",
                correcte,
                bonneProposition.getTexte()
            ));
        }

        // Calcul du score réel en pourcentage
        double score = ((double) nbCorrectes / questions.size()) * 100.0;
        double seuilRequis = formation.getScoreMinCertification();
        ResultatEnum statutFinal = score >= seuilRequis ? ResultatEnum.REUSSI : ResultatEnum.ECHOUE;

        evaluation.setScore(score);
        evaluation.setStatut(statutFinal);
        evaluationRepo.save(evaluation);

        // Génération automatique du certificat si examen final réussi
        if (dto.getType() == TypeEvalEnum.EXAMEN_FINAL && statutFinal == ResultatEnum.REUSSI) {
            genererCertificatSiNecessaire(utilisateur, formation, score);
        }

        return new ResultatQuizDto(
            evaluation.getId(),
            score,
            statutFinal,
            questions.size(),
            nbCorrectes,
            details
        );
    }

    /** Génère le certificat uniquement s'il n'existe pas déjà pour cette formation */
    private void genererCertificatSiNecessaire(Utilisateur utilisateur, Formation formation, double score) {
        if (certificatRepo.existsByUtilisateurIdAndFormationId(utilisateur.getId(), formation.getId())) {
            return; // déjà certifié, on ne duplique pas
        }
        Certificat certificat = new Certificat();
        certificat.setIdentifiantUnique(idGenerateur.generer());
        certificat.setUtilisateur(utilisateur);
        certificat.setFormation(formation);
        certificat.setScoreFinal(score);
        certificat.setUrlPdf("/certificats/" + certificat.getIdentifiantUnique() + ".pdf");
        certificatRepo.save(certificat);
    }
}
