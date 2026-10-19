package projet.Formation_IT3.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO pour mettre à jour la progression d'un apprenant sur une leçon.
 */
@Data
public class ProgressionRequestDto {

    /** ID de la leçon concernée */
    @NotNull(message = "L'ID de la leçon est obligatoire")
    private Long leconId;

    /** Indique si la leçon est marquée comme complète */
    private boolean estComplete = false;

    /** Position actuelle dans la vidéo en secondes (pour reprendre) */
    private Integer positionVideo = 0;
}
