package projet.Formation_IT3.modeles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité de traçabilité : enregistre quelle proposition l'employé a choisie
 * pour chaque question, lors d'une tentative d'évaluation donnée.
 *
 * Permet de reconstituer après coup le détail de la correction
 * (quelles questions ont été réussies/ratées) pour affichage côté employé
 * ou consultation côté formateur.
 */
@Entity
@Table(name = "reponses_employe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReponseEmploye {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** L'évaluation (tentative) à laquelle appartient cette réponse */
    @ManyToOne
    @JoinColumn(name = "evaluation_id", nullable = false)
    @JsonIgnore
    private Evaluation evaluation;

    /** La question concernée */
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /** La proposition que l'employé a choisie */
    @ManyToOne
    @JoinColumn(name = "proposition_choisie_id", nullable = false)
    private Proposition propositionChoisie;

    /** Calculé au moment de la correction : true si le choix était le bon */
    @Column(nullable = false)
    private boolean correcte;
}
