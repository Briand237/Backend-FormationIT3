package projet.Formation_IT3.dtos;

import projet.Formation_IT3.enums.NiveauEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO pour la création et la mise à jour d'une formation.
 */
@Data
public class FormationRequestDto {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotBlank(message = "Le domaine est obligatoire")
    private String domaine;

    @NotNull(message = "Le niveau est obligatoire")
    private NiveauEnum niveau;

    @NotNull(message = "La durée en heures est obligatoire")
    @Positive(message = "La durée doit être positive")
    private Integer dureeHeures;

    /** Score minimum pour obtenir le certificat (défaut : 80%) */
    private Double scoreMinCertification = 80.0;

    private String imageUrl;

    /** ID du formateur responsable */
    private Long formateurId;

    private boolean publiee = false;
}
