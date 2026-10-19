package projet.Formation_IT3.controller;

import projet.Formation_IT3.dtos.ApiResponseDto;
import projet.Formation_IT3.modeles.Certificat;
import projet.Formation_IT3.modeles.Utilisateur;
import projet.Formation_IT3.service.CertificatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion et le téléchargement des certificats.
 *
 * Routes disponibles :
 * GET /api/certificats/mes-certificats        → Tous mes certificats obtenus
 * GET /api/certificats/{id}                   → Détail d'un certificat
 * GET /api/certificats/verifier/{identifiant} → Vérification publique (KA-XXXX)
 * POST /api/certificats/generer               → Générer manuellement un certificat
 */
@RestController
@RequestMapping("/api/certificats")
@CrossOrigin(origins = "http://localhost:4200")
public class CertificatController {

    @Autowired
    private CertificatService certificatService;

    /** Récupère l'utilisateur connecté depuis le contexte Spring Security */
    private Utilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Utilisateur) auth.getPrincipal();
    }

    // ==================== GET ====================

    /**
     * Retourne tous les certificats obtenus par l'utilisateur connecté.
     * Correspond à la page "Mes Certificats" de l'interface KAOLO.
     */
    @GetMapping("/mes-certificats")
    public ResponseEntity<ApiResponseDto<List<Certificat>>> getMesCertificats() {
        Utilisateur utilisateur = getUtilisateurConnecte();
        List<Certificat> certificats = certificatService.getMesCertificats(utilisateur.getId());
        return ResponseEntity.ok(ApiResponseDto.succes(
            certificats.size() + " certificat(s) obtenu(s)", certificats));
    }

    /** Récupère le détail d'un certificat par son ID interne */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Certificat>> getParId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponseDto.succes(
                "Certificat trouvé", certificatService.getParId(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    /**
     * Route publique de vérification d'un certificat par son identifiant unique (KA-XXXX).
     * Permet à toute personne de vérifier l'authenticité d'un certificat KAOLO.
     *
     * Exemple : GET /api/certificats/verifier/KA-4821
     */
    @GetMapping("/verifier/{identifiantUnique}")
    public ResponseEntity<ApiResponseDto<Certificat>> verifier(
            @PathVariable String identifiantUnique) {
        try {
            Certificat certificat = certificatService.getParIdentifiantUnique(identifiantUnique);
            return ResponseEntity.ok(ApiResponseDto.succes(
                "Certificat vérifié et valide ✓", certificat));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur("Aucun certificat trouvé avec l'identifiant : " + identifiantUnique));
        }
    }

    // ==================== POST ====================

    /**
     * Génère manuellement un certificat pour l'utilisateur connecté.
     * Normalement appelé automatiquement par EvaluationController après réussite.
     * Ce endpoint permet une génération manuelle si nécessaire (ex: admin).
     *
     * Paramètres :
     *  - formationId : ID de la formation certifiée
     *  - scoreFinal  : Score obtenu à l'examen (ex: 87.5)
     */
    @PostMapping("/generer")
    public ResponseEntity<ApiResponseDto<Certificat>> generer(
            @RequestParam Long formationId,
            @RequestParam Double scoreFinal) {
        try {
            Utilisateur utilisateur = getUtilisateurConnecte();
            Certificat certificat = certificatService.generer(
                utilisateur.getId(), formationId, scoreFinal);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.succes(
                    "Certificat généré avec succès ! ID : " + certificat.getIdentifiantUnique(),
                    certificat));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }
}
