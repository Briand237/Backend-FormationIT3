package projet.Formation_IT3.controller;

import projet.Formation_IT3.dtos.ApiResponseDto;
import projet.Formation_IT3.dtos.ProgressionRequestDto;
import projet.Formation_IT3.modeles.Progression;
import projet.Formation_IT3.modeles.Utilisateur;
import projet.Formation_IT3.service.ProgressionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour le suivi de la progression des apprenants.
 *
 * Routes disponibles :
 * POST /api/progression          → Sauvegarder/mettre à jour la progression d'une leçon
 * GET  /api/progression          → Ma progression globale (toutes leçons)
 * GET  /api/progression/formation/{id}/pourcentage → % de complétion d'une formation
 */
@RestController
@RequestMapping("/api/progression")
@CrossOrigin(origins = "http://localhost:4200")
public class ProgressionController {

    @Autowired
    private ProgressionService progressionService;

    /** Récupère l'utilisateur connecté depuis le contexte Spring Security */
    private Utilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Utilisateur) auth.getPrincipal();
    }

    // ==================== POST ====================

    /**
     * Sauvegarde ou met à jour la progression d'une leçon pour l'utilisateur connecté.
     * Appelé automatiquement lors de la navigation dans les leçons.
     *
     * Corps de requête :
     * {
     *   "leconId": 5,
     *   "estComplete": true,
     *   "positionVideo": 525
     * }
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<Progression>> sauvegarder(
            @Valid @RequestBody ProgressionRequestDto dto) {
        try {
            Utilisateur utilisateur = getUtilisateurConnecte();
            Progression progression = progressionService.sauvegarder(utilisateur.getId(), dto);
            return ResponseEntity.ok(ApiResponseDto.succes("Progression sauvegardée", progression));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    // ==================== GET ====================

    /** Récupère toutes les progressions de leçons de l'utilisateur connecté */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<Progression>>> getMesProgressions() {
        Utilisateur utilisateur = getUtilisateurConnecte();
        List<Progression> progressions = progressionService.getMesProgressions(utilisateur.getId());
        return ResponseEntity.ok(ApiResponseDto.succes(
            progressions.size() + " progression(s) enregistrée(s)", progressions));
    }

    /**
     * Calcule et retourne le pourcentage de complétion d'une formation.
     * Paramètre totalLecons : nombre total de leçons dans la formation.
     *
     * Exemple : GET /api/progression/formation/1/pourcentage?totalLecons=24
     */
    @GetMapping("/formation/{formationId}/pourcentage")
    public ResponseEntity<ApiResponseDto<Double>> getPourcentage(
            @PathVariable Long formationId,
            @RequestParam long totalLecons) {
        Utilisateur utilisateur = getUtilisateurConnecte();
        double pourcentage = progressionService.calculerPourcentage(
            utilisateur.getId(), formationId, totalLecons);
        return ResponseEntity.ok(ApiResponseDto.succes(
            "Progression : " + String.format("%.1f", pourcentage) + "%", pourcentage));
    }
}
