package  projet.Formation_IT3.service;

import  projet.Formation_IT3.dtos.EvaluationRequestDto;
import  projet.Formation_IT3.enums.ResultatEnum;
import  projet.Formation_IT3.modeles.Evaluation;
import  projet.Formation_IT3.modeles.Formation;
import  projet.Formation_IT3.modeles.Utilisateur;
import  projet.Formation_IT3.repository.EvaluationRepository;
import  projet.Formation_IT3.repository.FormationRepository;
import  projet.Formation_IT3.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service gérant la soumission et le suivi des évaluations (quiz et examens).
 * Détermine automatiquement le résultat selon le score minimum requis.
 */
@Service
public class EvaluationService {

    @Autowired
    private EvaluationRepository evaluationRepo;

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private FormationRepository formationRepo;

    /**
     * Soumet et enregistre le résultat d'une évaluation.
     * Compare le score au seuil de la formation pour déterminer REUSSI ou ECHOUE.
     *
     * @param utilisateurId ID de l'apprenant
     * @param dto Les données de l'évaluation soumise
     * @return L'évaluation sauvegardée avec son résultat
     */
    public Evaluation soumettre(Long utilisateurId, EvaluationRequestDto dto) {
        Utilisateur utilisateur = utilisateurRepo.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + utilisateurId));

        Formation formation = formationRepo.findById(dto.getFormationId())
            .orElseThrow(() -> new RuntimeException("Formation introuvable : " + dto.getFormationId()));

        // Calcul du numéro de tentative (nombre de tentatives précédentes + 1)
        int tentative = evaluationRepo.countByUtilisateurIdAndFormationId(utilisateurId, dto.getFormationId()) + 1;

        // Détermination automatique du résultat selon le score minimum requis
        ResultatEnum resultat = dto.getScore() >= formation.getScoreMinCertification()
            ? ResultatEnum.REUSSI
            : ResultatEnum.ECHOUE;

        Evaluation evaluation = new Evaluation();
        evaluation.setTitre(dto.getTitre());
        evaluation.setType(dto.getType());
        evaluation.setUtilisateur(utilisateur);
        evaluation.setFormation(formation);
        evaluation.setScore(dto.getScore());
        evaluation.setStatut(resultat);
        evaluation.setTentativeNum(tentative);

        return evaluationRepo.save(evaluation);
    }

    /** Historique complet des évaluations d'un utilisateur */
    public List<Evaluation> getHistorique(Long utilisateurId) {
        return evaluationRepo.findByUtilisateurId(utilisateurId);
    }

    /** Évaluations d'un utilisateur pour une formation spécifique */
    public List<Evaluation> getParFormation(Long utilisateurId, Long formationId) {
        return evaluationRepo.findByUtilisateurIdAndFormationId(utilisateurId, formationId);
    }

    /** Récupère une évaluation par son ID */
    public Evaluation getParId(Long id) {
        return evaluationRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Évaluation introuvable : " + id));
    }
}
