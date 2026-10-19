package projet.Formation_IT3.modeles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import projet.Formation_IT3.enums.TypeEvalEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une question de QCM (Quiz à Choix Multiples).
 * Une question appartient à une formation, et peut être rattachée soit à
 * un module précis (quiz de fin de module), soit à aucun module
 * (question de l'examen final global de la formation).
 *
 * Créée par un formateur/admin via l'interface de gestion des formations,
 * répondue par un employé lors d'un quiz ou d'un examen final.
 */
@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Texte de la question affiché à l'employé */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String enonce;

    /** Type d'évaluation auquel cette question appartient (QUIZ ou EXAMEN_FINAL) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEvalEnum type;

    /** La formation à laquelle appartient cette question (toujours renseignée) */
    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    /**
     * Le module concerné si c'est une question de quiz de module (type = QUIZ).
     * Reste NULL si c'est une question d'examen final (type = EXAMEN_FINAL),
     * car l'examen final porte sur l'ensemble de la formation.
     */
    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    /** Ordre d'affichage de la question dans le quiz */
    @Column(nullable = false)
    private Integer ordre = 1;

    /**
     * Liste des 4 propositions de réponse (dont une seule est correcte).
     * @JsonIgnore n'est PAS utilisé ici côté Proposition->Question car
     * Proposition n'a pas de référence retour vers Question dans ce sens
     * (voir Proposition.java) — pas de risque de boucle infinie.
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Proposition> propositions = new ArrayList<>();
}
