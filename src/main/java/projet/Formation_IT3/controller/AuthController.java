package projet.Formation_IT3.controller;

import projet.Formation_IT3.dtos.ApiResponseDto;
import projet.Formation_IT3.dtos.AuthResponseDto;
import projet.Formation_IT3.dtos.ConnexionRequestDto;
import projet.Formation_IT3.dtos.InscriptionRequestDto;
import projet.Formation_IT3.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour l'authentification des utilisateurs KAOLO.
 *
 * Routes disponibles (publiques - sans JWT requis) :
 * POST /api/auth/inscription  → Créer un nouveau compte employé
 * POST /api/auth/connexion    → Se connecter et obtenir un token JWT
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Inscrit un nouvel employé sur la plateforme KAOLO.
     *
     * Corps de la requête :
     * {
     *   "nomComplet": "Jean Dupont",
     *   "email": "jean.dupont@kaolo.com",
     *   "idEmploye": "KA-0042",
     *   "departement": "DevOps",
     *   "motDePasse": "MonMotDePasse123"
     * }
     */
    @PostMapping("/inscription")
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> inscrire(
            @Valid @RequestBody InscriptionRequestDto dto) {
        try {
            AuthResponseDto response = authService.inscrire(dto);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDto.succes("Compte créé avec succès ! Bienvenue chez KAOLO.", response));
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }
    

    /**
     * Authentifie un utilisateur et retourne un token JWT.
     *
     * Corps de la requête :
     * {
     *   "email": "jean.dupont@kaolo.com",
     *   "motDePasse": "MonMotDePasse123"
     * }
     *
     * Réponse : token JWT à utiliser dans Authorization: Bearer <token>
     */
    @PostMapping("/connexion")
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> connecter(
            @Valid @RequestBody ConnexionRequestDto dto) {
        try {
            AuthResponseDto response = authService.connecter(dto);
            return ResponseEntity.ok(ApiResponseDto.succes("Connexion réussie !", response));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDto.erreur("Identifiants invalides. Vérifiez votre email et mot de passe."));
        }
    }
}
