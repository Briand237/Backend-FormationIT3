package projet.Formation_IT3.dtos;

import projet.Formation_IT3.enums.TypeLeconEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO pour la création et la mise à jour d'une leçon.
 */
@Data
public class LeconRequestDto {

    @NotBlank(message = "Le titre de la leçon est obligatoire")
    private String titre;

    @NotNull(message = "Le type de leçon est obligatoire")
    private TypeLeconEnum type;

    /** URL de la vidéo (requis si type = VIDEO) */
    private String urlVideo;

    /** Contenu textuel (requis si type = TEXTE) */
    private String contenuTexte;

    /** Durée de la leçon en secondes */
    private Integer dureeSecondes;

    @NotNull(message = "L'ordre est obligatoire")
    @Positive(message = "L'ordre doit être un entier positif")
    private Integer ordre;

    /** ID du module auquel appartient cette leçon */
    @NotNull(message = "L'ID du module est obligatoire")
    private Long moduleId;
}
