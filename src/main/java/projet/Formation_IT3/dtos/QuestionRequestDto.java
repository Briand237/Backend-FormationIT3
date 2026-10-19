package projet.Formation_IT3.dtos;

import projet.Formation_IT3.enums.TypeEvalEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * DTO de requête pour créer/modifier une question avec ses propositions.
 * Utilisé exclusivement par le formateur/admin dans l'interface de gestion.
 */
@Data
public class QuestionRequestDto {

    @NotBlank(message = "L'énoncé de la question est obligatoire")
    private String enonce;

    @NotNull(message = "Le type (QUIZ ou EXAMEN_FINAL) est obligatoire")
    private TypeEvalEnum type;

    @NotNull(message = "L'ID de la formation est obligatoire")
    private Long formationId;

    /** Optionnel : renseigné uniquement si type = QUIZ (quiz de module précis) */
    private Long moduleId;

    private Integer ordre = 1;

    /** Exactement 4 propositions attendues, dont une seule correcte (validé en service) */
    @NotEmpty(message = "Au moins 2 propositions sont requises")
    @Size(min = 2, max = 6, message = "Entre 2 et 6 propositions")
    @Valid
    private List<PropositionRequestDto> propositions;
}
