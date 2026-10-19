package projet.Formation_IT3.enums;

/**
 * Résultat d'une évaluation (quiz ou examen).
 */
public enum ResultatEnum {
    /** Score atteint le seuil minimum requis */
    REUSSI,
    /** Score inférieur au seuil minimum requis */
    ECHOUE,
    /** Évaluation soumise, en attente de correction */
    EN_ATTENTE
}
