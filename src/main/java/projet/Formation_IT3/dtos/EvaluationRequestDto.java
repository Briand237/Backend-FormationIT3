package projet.Formation_IT3.dtos;

import projet.Formation_IT3.enums.TypeEvalEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO pour soumettre le résultat d'une évaluation (quiz ou examen).
 */
@Data
public class EvaluationRequestDto {

    @NotBlank(message = "Le titre de l'évaluation est obligatoire")
    private String titre;

    @NotNull(message = "Le type d'évaluation est obligatoire")
    private TypeEvalEnum type;

    /** ID de la formation concernée */
    @NotNull(message = "L'ID de la formation est obligatoire")
    private Long formationId;

    /** Score obtenu entre 0 et 100 */
    @NotNull(message = "Le score est obligatoire")
    @Min(value = 0, message = "Le score minimum est 0")
    @Max(value = 100, message = "Le score maximum est 100")
    private Double score;
}
