package projet.Formation_IT3.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO générique pour uniformiser toutes les réponses de l'API.
 * Chaque réponse contient : succès, message, données et horodatage.
 *
 * @param <T> Type des données retournées
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto<T> {

    /** Indique si la requête a réussi */
    private boolean succes;

    /** Message descriptif du résultat */
    private String message;

    /** Données retournées (null en cas d'erreur) */
    private T data;

    /** Horodatage de la réponse */
    private LocalDateTime timestamp = LocalDateTime.now();

    /** Constructeur pour une réponse de succès avec données */
    public static <T> ApiResponseDto<T> succes(String message, T data) {
        return new ApiResponseDto<>(true, message, data, LocalDateTime.now());
    }

    /** Constructeur pour une réponse de succès sans données */
    public static <T> ApiResponseDto<T> succes(String message) {
        return new ApiResponseDto<>(true, message, null, LocalDateTime.now());
    }

    /** Constructeur pour une réponse d'erreur */
    public static <T> ApiResponseDto<T> erreur(String message) {
        return new ApiResponseDto<>(false, message, null, LocalDateTime.now());
    }
}
