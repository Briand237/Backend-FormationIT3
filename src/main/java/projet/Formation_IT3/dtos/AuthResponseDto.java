package projet.Formation_IT3.dtos;

import projet.Formation_IT3.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse après une connexion réussie.
 * Contient le token JWT et les informations de base de l'utilisateur.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

    /** Token JWT à inclure dans les en-têtes Authorization des requêtes suivantes */
    private String token;

    /** Type du token (toujours "Bearer") */
    private String type = "Bearer";

    /** Identifiant de l'utilisateur connecté */
    private Long id;

    /** Nom complet */
    private String nomComplet;

    /** Email professionnel */
    private String email;

    /** Rôle sur la plateforme */
    private RoleEnum role;

    public AuthResponseDto(String token, Long id, String nomComplet, String email, RoleEnum role) {
        this.token = token;
        this.id = id;
        this.nomComplet = nomComplet;
        this.email = email;
        this.role = role;
    }
}
