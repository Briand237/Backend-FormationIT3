package projet.Formation_IT3.modeles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant une proposition de réponse pour une Question de QCM.
 * Chaque question possède 4 propositions, dont une seule a estCorrecte = true.
 *
 * SÉCURITÉ IMPORTANTE : le champ estCorrecte ne doit JAMAIS être envoyé
 * à l'employé pendant qu'il passe le quiz (sinon il pourrait voir la
 * réponse dans le code source de la page / la réponse réseau). Voir
 * QuestionEmployeDto qui filtre ce champ avant envoi au frontend employé.
 * Ce champ n'est visible que dans les réponses destinées au FORMATEUR
 * (gestion des questions) et après correction (résultat détaillé).
 */
@Entity
@Table(name = "propositions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proposition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Texte de la proposition affiché comme option de réponse */
    @Column(nullable = false)
    private String texte;

    /** Indique si cette proposition est la bonne réponse */
    @Column(nullable = false)
    private boolean estCorrecte = false;

    /**
     * La question à laquelle appartient cette proposition.
     * @JsonIgnore empêche la boucle infinie Question -> propositions ->
     * Proposition -> question -> propositions -> ... lors de la sérialisation JSON.
     */
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;
}
