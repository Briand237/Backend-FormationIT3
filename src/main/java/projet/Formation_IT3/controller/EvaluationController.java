package projet.Formation_IT3.controller;

import projet.Formation_IT3.dtos.ApiResponseDto;
import projet.Formation_IT3.dtos.EvaluationRequestDto;
import projet.Formation_IT3.modeles.Evaluation;
import projet.Formation_IT3.modeles.Utilisateur;
import projet.Formation_IT3.service.CertificatService;
import projet.Formation_IT3.service.EvaluationService;
import projet.Formation_IT3.enums.ResultatEnum;
import projet.Formation_IT3.enums.TypeEvalEnum;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la soumission et la consultation des évaluations.
 *
 * Routes disponibles :
 * POST /api/evaluations              → Soumettre une évaluation (quiz ou examen)
 * GET  /api/evaluations/historique   → Mon historique d'évaluations
 * GET  /api/evaluations/formation/{id} → Mes évaluations pour une formation
 * GET  /api/evaluations/{id}         → Détail d'une évaluation
 */
@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin(origins = "http://localhost:4200")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private CertificatService certificatService;

    /** Récupère l'utilisateur connecté depuis le contexte Spring Security */
    private Utilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Utilisateur) auth.getPrincipal();
    }

    // ==================== POST ====================

    /**
     * Soumet une évaluation et calcule automatiquement le résultat.
     * Si c'est un EXAMEN_FINAL réussi, génère automatiquement le certificat.
     *
     * Corps de requête :
     * {
     *   "titre": "Examen Final Kubernetes",
     *   "type": "EXAMEN_FINAL",
     *   "formationId": 1,
     *   "score": 85.5
     * }
     *
     * Réponse : résultat REUSSI ou ECHOUE + certificat si examen final réussi
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<Evaluation>> soumettre(
            @Valid @RequestBody EvaluationRequestDto dto) {
        try {
            Utilisateur utilisateur = getUtilisateurConnecte();

            // Enregistrement de l'évaluation avec calcul automatique du résultat
            Evaluation evaluation = evaluationService.soumettre(utilisateur.getId(), dto);

            // Si c'est un examen final réussi → génération automatique du certificat
            if (dto.getType() == TypeEvalEnum.EXAMEN_FINAL
                    && evaluation.getStatut() == ResultatEnum.REUSSI) {
                certificatService.generer(
                    utilisateur.getId(),
                    dto.getFormationId(),
                    dto.getScore()
                );
                return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.succes(
                        "Félicitations ! Examen réussi avec " + dto.getScore() +
                        "%. Votre certificat a été généré automatiquement.", evaluation));
            }

            // Résultat selon le statut de l'évaluation
            String message = evaluation.getStatut() == ResultatEnum.REUSSI
                ? "Évaluation réussie avec " + dto.getScore() + "%"
                : "Score insuffisant (" + dto.getScore() + "%). Vous pouvez repasser l'examen.";

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.succes(message, evaluation));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    // ==================== GET ====================

    /** Retourne l'historique complet des évaluations de l'utilisateur connecté */
    @GetMapping("/historique")
    public ResponseEntity<ApiResponseDto<List<Evaluation>>> getHistorique() {
        Utilisateur utilisateur = getUtilisateurConnecte();
        List<Evaluation> evaluations = evaluationService.getHistorique(utilisateur.getId());
        return ResponseEntity.ok(ApiResponseDto.succes(
            evaluations.size() + " évaluation(s) dans l'historique", evaluations));
    }

    /** Retourne les évaluations de l'utilisateur pour une formation spécifique */
    @GetMapping("/formation/{formationId}")
    public ResponseEntity<ApiResponseDto<List<Evaluation>>> getParFormation(
            @PathVariable Long formationId) {
        Utilisateur utilisateur = getUtilisateurConnecte();
        List<Evaluation> evaluations = evaluationService.getParFormation(
            utilisateur.getId(), formationId);
        return ResponseEntity.ok(ApiResponseDto.succes(
            evaluations.size() + " évaluation(s) pour cette formation", evaluations));
    }

    /** Retourne le détail d'une évaluation par son ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Evaluation>> getParId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponseDto.succes(
                "Évaluation trouvée", evaluationService.getParId(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }
}
