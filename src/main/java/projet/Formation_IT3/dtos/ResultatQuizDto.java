package projet.Formation_IT3.dtos;

import projet.Formation_IT3.enums.ResultatEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de réponse renvoyé à l'employé après correction automatique du quiz.
 * Contient le score, le résultat global, et le détail question par question
 * (pour lui permettre de voir ce qu'il a raté et la bonne réponse, à des
 * fins pédagogiques — seulement APRÈS soumission, jamais avant).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultatQuizDto {

    private Long evaluationId;
    private Double score;
    private ResultatEnum statut;
    private Integer nombreQuestionsTotal;
    private Integer nombreReponsesCorrectes;
    private List<DetailReponseDto> details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailReponseDto {
        private Long questionId;
        private String enonce;
        private String propositionChoisieTexte;
        private boolean correcte;
        /** Révélée uniquement maintenant, après soumission */
        private String bonneReponseTexte;
    }
}
