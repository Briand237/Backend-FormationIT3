package projet.Formation_IT3.controller;

import projet.Formation_IT3.dtos.ApiResponseDto;
import projet.Formation_IT3.dtos.LeconRequestDto;
import projet.Formation_IT3.modeles.Lecon;
import projet.Formation_IT3.modeles.Module;
import projet.Formation_IT3.repository.LeconRepository;
import projet.Formation_IT3.repository.ModuleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des leçons d'un module.
 *
 * Routes disponibles :
 * GET    /api/lecons/module/{moduleId}  → Toutes les leçons d'un module
 * GET    /api/lecons/{id}               → Détail d'une leçon
 * POST   /api/lecons                    → Créer une leçon (FORMATEUR/ADMIN)
 * PUT    /api/lecons/{id}               → Modifier une leçon (FORMATEUR/ADMIN)
 * DELETE /api/lecons/{id}               → Supprimer une leçon (ADMIN)
 */
@RestController
@RequestMapping("/api/lecons")
@CrossOrigin(origins = "http://localhost:4200")
public class LeconController {

    @Autowired
    private LeconRepository leconRepo;

    @Autowired
    private ModuleRepository moduleRepo;

    // ==================== GET ====================

    /** Récupère toutes les leçons d'un module, triées par ordre */
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<ApiResponseDto<List<Lecon>>> getParModule(
            @PathVariable Long moduleId) {
        List<Lecon> lecons = leconRepo.findByModuleIdOrderByOrdreAsc(moduleId);
        return ResponseEntity.ok(ApiResponseDto.succes(
            lecons.size() + " leçon(s) trouvée(s)", lecons));
    }

    /** Récupère une leçon par son identifiant */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Lecon>> getParId(@PathVariable Long id) {
        return leconRepo.findById(id)
            .map(l -> ResponseEntity.ok(ApiResponseDto.succes("Leçon trouvée", l)))
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur("Leçon introuvable avec l'ID : " + id)));
    }

    // ==================== POST ====================

    /**
     * Crée une nouvelle leçon dans un module.
     * Corps de requête exemple :
     * {
     *   "titre": "Introduction aux conteneurs",
     *   "type": "VIDEO",
     *   "urlVideo": "https://cdn.kaolo.com/videos/docker-intro.mp4",
     *   "dureeSecondes": 525,
     *   "ordre": 1,
     *   "moduleId": 1
     * }
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Lecon>> creer(
            @Valid @RequestBody LeconRequestDto dto) {
        try {
            // Récupération du module parent
            Module module = moduleRepo.findById(dto.getModuleId())
                .orElseThrow(() -> new RuntimeException(
                    "Module introuvable avec l'ID : " + dto.getModuleId()));

            // Construction de la leçon
            Lecon lecon = new Lecon();
            lecon.setTitre(dto.getTitre());
            lecon.setType(dto.getType());
            lecon.setUrlVideo(dto.getUrlVideo());
            lecon.setContenuTexte(dto.getContenuTexte());
            lecon.setDureeSecondes(dto.getDureeSecondes());
            lecon.setOrdre(dto.getOrdre());
            lecon.setModule(module);

            Lecon sauvegardee = leconRepo.save(lecon);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.succes("Leçon créée avec succès", sauvegardee));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    // ==================== PUT ====================

    /** Met à jour une leçon existante */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Lecon>> modifier(
            @PathVariable Long id,
            @Valid @RequestBody LeconRequestDto dto) {
        return leconRepo.findById(id).map(lecon -> {
            lecon.setTitre(dto.getTitre());
            lecon.setType(dto.getType());
            lecon.setUrlVideo(dto.getUrlVideo());
            lecon.setContenuTexte(dto.getContenuTexte());
            lecon.setDureeSecondes(dto.getDureeSecondes());
            lecon.setOrdre(dto.getOrdre());
            return ResponseEntity.ok(ApiResponseDto.succes(
                "Leçon mise à jour", leconRepo.save(lecon)));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponseDto.erreur("Leçon introuvable : " + id)));
    }

    // ==================== DELETE ====================

    /** Supprime définitivement une leçon */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> supprimer(@PathVariable Long id) {
        if (!leconRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur("Leçon introuvable : " + id));
        }
        leconRepo.deleteById(id);
        return ResponseEntity.ok(ApiResponseDto.succes("Leçon supprimée avec succès"));
    }
}
