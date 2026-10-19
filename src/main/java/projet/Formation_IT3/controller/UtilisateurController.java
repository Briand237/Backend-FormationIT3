package projet.Formation_IT3.controller;

import projet.Formation_IT3.dtos.ApiResponseDto;
import projet.Formation_IT3.modeles.Utilisateur;
import projet.Formation_IT3.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des profils utilisateurs.
 *
 * Routes disponibles :
 * GET  /api/utilisateurs/profil        → Mon profil (utilisateur connecté)
 * GET  /api/utilisateurs               → Tous les utilisateurs (ADMIN)
 * GET  /api/utilisateurs/{id}          → Profil d'un utilisateur (ADMIN)
 * PUT  /api/utilisateurs/{id}/activer  → Activer/désactiver un compte (ADMIN)
 * DELETE /api/utilisateurs/{id}        → Supprimer un compte (ADMIN)
 */
@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "http://localhost:4200")
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    /** Récupère l'utilisateur actuellement connecté */
    private Utilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Utilisateur) auth.getPrincipal();
    }

    // ==================== GET ====================

    /** Retourne le profil de l'utilisateur actuellement connecté */
    @GetMapping("/profil")
    public ResponseEntity<ApiResponseDto<Utilisateur>> getMonProfil() {
        Utilisateur utilisateur = getUtilisateurConnecte();
        return ResponseEntity.ok(ApiResponseDto.succes("Profil récupéré", utilisateur));
    }

    /** Liste tous les utilisateurs de la plateforme (réservé aux admins) */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<Utilisateur>>> getTous() {
        List<Utilisateur> utilisateurs = utilisateurRepo.findAll();
        return ResponseEntity.ok(ApiResponseDto.succes(
            utilisateurs.size() + " utilisateur(s) enregistré(s)", utilisateurs));
    }

    /** Récupère un utilisateur spécifique par son ID (réservé aux admins) */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Utilisateur>> getParId(@PathVariable Long id) {
        return utilisateurRepo.findById(id)
            .map(u -> ResponseEntity.ok(ApiResponseDto.succes("Utilisateur trouvé", u)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur("Utilisateur introuvable : " + id)));
    }

    // ==================== PUT ====================

    /**
     * Active ou désactive le compte d'un utilisateur.
     * Un compte désactivé ne peut plus se connecter.
     *
     * Exemple : PUT /api/utilisateurs/5/activer?actif=false
     */
    @PutMapping("/{id}/activer")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Utilisateur>> changerStatut(
            @PathVariable Long id,
            @RequestParam boolean actif) {
        return utilisateurRepo.findById(id).map(utilisateur -> {
            utilisateur.setActif(actif);
            Utilisateur sauvegarde = utilisateurRepo.save(utilisateur);
            String msg = actif ? "Compte activé avec succès" : "Compte désactivé";
            return ResponseEntity.ok(ApiResponseDto.succes(msg, sauvegarde));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponseDto.erreur("Utilisateur introuvable : " + id)));
    }

    // ==================== DELETE ====================

    /** Supprime définitivement le compte d'un utilisateur (SUPER_ADMIN uniquement) */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> supprimer(@PathVariable Long id) {
        if (!utilisateurRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur("Utilisateur introuvable : " + id));
        }
        utilisateurRepo.deleteById(id);
        return ResponseEntity.ok(ApiResponseDto.succes("Utilisateur supprimé définitivement"));
    }
}
