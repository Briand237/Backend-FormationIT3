package projet.Formation_IT3.dtos;

import projet.Formation_IT3.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de requête pour l'inscription d'un nouvel employé.
 * Les annotations @NotBlank et @Email valident automatiquement les champs.
 */
@Data
public class InscriptionRequestDto {

    @NotBlank(message = "Le nom complet est obligatoire")
    private String nomComplet;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "L'ID employé est obligatoire")
    @Pattern(regexp = "KA-[0-9]{4}", message = "Format ID employé invalide (KA-XXXX)")
    private String idEmploye;

    @NotBlank(message = "Le département est obligatoire")
    private String departement;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;
}


