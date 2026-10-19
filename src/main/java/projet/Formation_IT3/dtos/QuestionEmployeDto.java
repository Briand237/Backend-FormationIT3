package projet.Formation_IT3.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de question envoyé à l'EMPLOYÉ pendant qu'il passe un quiz/examen.
 *
 * SÉCURITÉ CRITIQUE : ce DTO NE CONTIENT PAS le champ estCorrecte des
 * propositions. Si on envoyait directement l'entité Question/Proposition
 * en JSON, un employé un peu curieux pourrait ouvrir l'onglet Network
 * de son navigateur et lire la bonne réponse directement dans la requête
 * GET avant même de répondre. Ce DTO filtre cette information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionEmployeDto {

    private Long id;
    private String enonce;
    private Integer ordre;

    /** Liste des propositions SANS l'information estCorrecte */
    private List<PropositionEmployeDto> propositions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropositionEmployeDto {
        private Long id;
        private String texte;
        // PAS de champ estCorrecte ici, volontairement
    }
}
