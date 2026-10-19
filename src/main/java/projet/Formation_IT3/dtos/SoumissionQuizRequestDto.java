package projet.Formation_IT3.dtos;

import projet.Formation_IT3.enums.TypeEvalEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO de requête envoyé par l'employé lors de la soumission d'un quiz/examen.
 *
 * IMPORTANT : on n'envoie PAS de score calculé côté frontend (contrairement
 * à l'ancienne version avec le slider). On envoie uniquement les réponses
 * choisies ; c'est le BACKEND qui calcule le score en comparant avec les
 * vraies bonnes réponses stockées en base — impossible à truquer côté client.
 */
@Data
public class SoumissionQuizRequestDto {

    @NotNull(message = "Le type d'évaluation est obligatoire")
    private TypeEvalEnum type;

    @NotNull(message = "L'ID de la formation est obligatoire")
    private Long formationId;

    /** Renseigné uniquement si type = QUIZ (quiz d'un module précis) */
    private Long moduleId;

    /**
     * Map associant chaque ID de question à l'ID de la proposition choisie
     * par l'employé. Exemple : { "1": "3", "2": "8", "3": "11" }
     * signifie "pour la question 1, j'ai choisi la proposition 3", etc.
     */
    @NotEmpty(message = "Au moins une réponse est requise")
    private Map<Long, Long> reponses;
}
