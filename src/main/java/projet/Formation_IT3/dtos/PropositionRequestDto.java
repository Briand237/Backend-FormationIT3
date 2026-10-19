package projet.Formation_IT3.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO d'une proposition de réponse, imbriqué dans QuestionRequestDto.
 */
@Data
public class PropositionRequestDto {

    @NotBlank(message = "Le texte de la proposition est obligatoire")
    private String texte;

    private boolean estCorrecte = false;
}
