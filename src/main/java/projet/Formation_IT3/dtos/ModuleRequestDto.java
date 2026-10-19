package projet.Formation_IT3.dtos;

import projet.Formation_IT3.enums.TypeLeconEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO pour la création et la mise à jour d'un module.
 */
@Data
public class ModuleRequestDto {

    @NotBlank(message = "Le titre du module est obligatoire")
    private String titre;

    @NotNull(message = "L'ordre est obligatoire")
    @Positive(message = "L'ordre doit être un entier positif")
    private Integer ordre;

    private Integer dureeMinutes;

    /** ID de la formation à laquelle ce module appartient */
    @NotNull(message = "L'ID de la formation est obligatoire")
    private Long formationId;
}
