package projet.Formation_IT3.configuration;

import projet.Formation_IT3.dtos.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour toute l'API KAOLO.
 * Intercepte les exceptions non traitées et retourne des réponses JSON cohérentes.
 * Évite les stacktraces brutes exposées aux clients.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les erreurs de validation des champs (@Valid, @NotBlank, etc.).
     * Retourne la liste de tous les champs invalides avec leur message d'erreur.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> erreurs = new HashMap<>();
        // Extraction de chaque erreur de champ avec son message personnalisé
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String champ = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            erreurs.put(champ, message);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponseDto.erreur("Erreurs de validation : " + erreurs.toString()));
    }

    /**
     * Gère les erreurs d'authentification (token invalide, expiré, absent).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleAuthError(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponseDto.erreur("Accès refusé : " + ex.getMessage()));
    }

    /**
     * Gère les accès interdits (rôle insuffisant pour l'action demandée).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponseDto.erreur("Vous n'avez pas les droits nécessaires pour cette action."));
    }

    /**
     * Gère toutes les autres exceptions non prévues.
     * Retourne un message générique sans exposer les détails techniques.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGenericError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponseDto.erreur("Erreur interne du serveur. Veuillez réessayer plus tard."));
    }
}
