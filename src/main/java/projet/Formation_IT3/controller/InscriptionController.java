package projet.Formation_IT3.controller;

import projet.Formation_IT3.dtos.ApiResponseDto;
import projet.Formation_IT3.modeles.Inscription;
import projet.Formation_IT3.service.InscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import projet.Formation_IT3.modeles.Utilisateur;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des inscriptions aux formations.
 *
 * Routes disponibles :
 * POST   /api/inscriptions/{formationId}      → S'inscrire à une formation
 * GET    /api/inscriptions/mes-inscriptions   → Mes formations inscrites
 * GET    /api/inscriptions/{id}               → Détail d'une inscription
 * DELETE /api/inscriptions/{id}               → Se désinscrire d'une formation
 */
@RestController
@RequestMapping("/api/inscriptions")
@CrossOrigin(origins = "http://localhost:4200")
public class InscriptionController {

    @Autowired
    private InscriptionService inscriptionService;

    /**
     * Récupère l'utilisateur actuellement connecté depuis le contexte Spring Security.
     * Cette méthode est utilisée dans chaque endpoint pour identifier l'apprenant.
     */
    private Utilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Utilisateur) auth.getPrincipal();
    }

    // ==================== POST ====================

    /**
     * Inscrit l'utilisateur connecté à la formation spécifiée.
     * Un utilisateur ne peut s'inscrire qu'une seule fois à la même formation.
     *
     * Exemple : POST /api/inscriptions/3
     * (inscrit l'utilisateur connecté à la formation d'ID 3)
     */
    @PostMapping("/{formationId}")
    public ResponseEntity<ApiResponseDto<Inscription>> sInscrire(
            @PathVariable Long formationId) {
        try {
            Utilisateur utilisateur = getUtilisateurConnecte();
            Inscription inscription = inscriptionService.inscrire(utilisateur.getId(), formationId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.succes("Inscription réussie ! Bonne formation.", inscription));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    // ==================== GET ====================

    /** Retourne toutes les formations auxquelles l'utilisateur connecté est inscrit */
    @GetMapping("/mes-inscriptions")
    public ResponseEntity<ApiResponseDto<List<Inscription>>> getMesInscriptions() {
        Utilisateur utilisateur = getUtilisateurConnecte();
        List<Inscription> inscriptions = inscriptionService.getMesInscriptions(utilisateur.getId());
        return ResponseEntity.ok(ApiResponseDto.succes(
            inscriptions.size() + " inscription(s)", inscriptions));
    }

    /** Récupère le détail d'une inscription par son ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Inscription>> getParId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponseDto.succes(
                "Inscription trouvée", inscriptionService.getParId(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    // ==================== DELETE ====================

    /** Désinscrit l'utilisateur d'une formation (supprime l'inscription) */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> desinscrire(@PathVariable Long id) {
        try {
            inscriptionService.desinscrire(id);
            return ResponseEntity.ok(ApiResponseDto.succes("Désinscription effectuée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }
}
