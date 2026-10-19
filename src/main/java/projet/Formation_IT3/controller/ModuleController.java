package projet.Formation_IT3.controller;

import projet.Formation_IT3.dtos.ApiResponseDto;
import projet.Formation_IT3.dtos.ModuleRequestDto;
import projet.Formation_IT3.modeles.Module;
import projet.Formation_IT3.service.ModuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des modules d'une formation.
 *
 * Routes disponibles :
 * GET    /api/modules/formation/{formationId}  → Modules d'une formation
 * GET    /api/modules/{id}                     → Détail d'un module
 * POST   /api/modules                          → Créer un module (FORMATEUR/ADMIN)
 * PUT    /api/modules/{id}                     → Modifier un module (FORMATEUR/ADMIN)
 * DELETE /api/modules/{id}                     → Supprimer un module (ADMIN)
 */
@RestController
@RequestMapping("/api/modules")
@CrossOrigin(origins = "\"http://localhost:4200\"")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    // ==================== GET ====================

    /** Récupère tous les modules d'une formation, triés par ordre */
    @GetMapping("/formation/{formationId}")
    public ResponseEntity<ApiResponseDto<List<Module>>> getParFormation(
            @PathVariable Long formationId) {
        List<Module> modules = moduleService.getParFormation(formationId);
        return ResponseEntity.ok(ApiResponseDto.succes(
            modules.size() + " module(s) trouvé(s)", modules));
    }

    /** Récupère le détail d'un module par son ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Module>> getParId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponseDto.succes("Module trouvé", moduleService.getParId(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    // ==================== POST ====================

    /**
     * Crée un nouveau module dans une formation.
     * Corps requis :
     * {
     *   "titre": "Fondamentaux Docker",
     *   "ordre": 1,
     *   "dureeMinutes": 90,
     *   "formationId": 1
     * }
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Module>> creer(
            @Valid @RequestBody ModuleRequestDto dto) {
        try {
            Module module = moduleService.creer(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.succes("Module créé avec succès", module));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    // ==================== PUT ====================

    /** Met à jour un module existant */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Module>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody ModuleRequestDto dto) {
        try {
            return ResponseEntity.ok(ApiResponseDto.succes(
                "Module mis à jour", moduleService.modifier(id, dto)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    // ==================== DELETE ====================

    /** Supprime un module et toutes ses leçons associées */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> supprimer(@PathVariable Long id) {
        try {
            moduleService.supprimer(id);
            return ResponseEntity.ok(ApiResponseDto.succes("Module supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }
}
