package projet.Formation_IT3.controller;

import projet.Formation_IT3.dtos.ApiResponseDto;
import projet.Formation_IT3.dtos.FormationRequestDto;
import projet.Formation_IT3.modeles.Formation;
import projet.Formation_IT3.service.FormationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion du catalogue de formations KAOLO.
 *
 * Routes disponibles :
 * GET    /api/formations              → Catalogue public (formations publiées)
 * GET    /api/formations/{id}         → Détail d'une formation
 * GET    /api/formations/recherche?q= → Recherche par mot-clé
 * GET    /api/formations/domaine/{d}  → Filtrer par domaine
 * POST   /api/formations              → Créer une formation (ADMIN/FORMATEUR)
 * PUT    /api/formations/{id}         → Modifier une formation (ADMIN/FORMATEUR)
 * PATCH  /api/formations/{id}/publier → Publier/dépublier (ADMIN)
 * DELETE /api/formations/{id}         → Supprimer une formation (ADMIN)
 */
@RestController
@RequestMapping("/api/formations")
@CrossOrigin(origins = "http://localhost:4200")
public class FormationController {

    @Autowired
    private FormationService formationService;

    // ==================== GET - LECTURE ====================

    /** Retourne toutes les formations publiées (catalogue visible par tous) */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<Formation>>> getCatalogue() {
        List<Formation> formations = formationService.getToutesPubliees();
        return ResponseEntity.ok(ApiResponseDto.succes(
            formations.size() + " formation(s) disponible(s)", formations));
    }

    /** Retourne toutes les formations y compris non publiées (admin uniquement) */
    @GetMapping("/admin/toutes")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<Formation>>> getToutesAdmin() {
        return ResponseEntity.ok(ApiResponseDto.succes("Toutes les formations", formationService.getToutesAdmin()));
    }

    /** Retourne le détail complet d'une formation par son ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Formation>> getParId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponseDto.succes("Formation trouvée", formationService.getParId(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    /** Recherche des formations par mot-clé dans le titre */
    @GetMapping("/recherche")
    public ResponseEntity<ApiResponseDto<List<Formation>>> rechercher(@RequestParam String q) {
        List<Formation> resultats = formationService.rechercherParMotCle(q);
        return ResponseEntity.ok(ApiResponseDto.succes(resultats.size() + " résultat(s) pour : " + q, resultats));
    }

    /** Filtre les formations par domaine technique */
    @GetMapping("/domaine/{domaine}")
    public ResponseEntity<ApiResponseDto<List<Formation>>> getParDomaine(@PathVariable String domaine) {
        return ResponseEntity.ok(ApiResponseDto.succes("Formations " + domaine, formationService.getParDomaine(domaine)));
    }

    // ==================== POST - CRÉATION ====================

    /**
     * Crée une nouvelle formation.
     * Réservé aux formateurs et administrateurs.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Formation>> creer(
            @Valid @RequestBody FormationRequestDto dto) {
        try {
            Formation creation = formationService.creer(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.succes("Formation créée avec succès", creation));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    // ==================== PUT - MODIFICATION ====================

    /** Met à jour une formation existante */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Formation>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody FormationRequestDto dto) {
        try {
            return ResponseEntity.ok(ApiResponseDto.succes("Formation mise à jour", formationService.modifier(id, dto)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    // ==================== PATCH - ACTION PARTIELLE ====================

    /** Publie ou dépublie une formation du catalogue */
    @PatchMapping("/{id}/publier")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Formation>> changerPublication(
            @PathVariable Long id,
            @RequestParam boolean publiee) {
        try {
            Formation f = formationService.changerStatutPublication(id, publiee);
            String msg = publiee ? "Formation publiée avec succès" : "Formation dépubliée";
            return ResponseEntity.ok(ApiResponseDto.succes(msg, f));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    // ==================== DELETE - SUPPRESSION ====================

    /** Supprime définitivement une formation et tous ses modules/leçons */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> supprimer(@PathVariable Long id) {
        try {
            formationService.supprimer(id);
            return ResponseEntity.ok(ApiResponseDto.succes("Formation supprimée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponseDto.erreur(e.getMessage()));
        }
    }
}
