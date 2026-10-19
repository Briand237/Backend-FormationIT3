package projet.Formation_IT3.modeles;

import projet.Formation_IT3.enums.ResultatEnum;
import projet.Formation_IT3.enums.TypeEvalEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entité représentant une évaluation (quiz ou examen) passée par un apprenant.
 * Chaque tentative crée un nouvel enregistrement d'évaluation.
 */
@Entity
@Table(name = "evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {

    /** Identifiant unique de l'évaluation */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre de l'évaluation */
    @Column(nullable = false)
    private String titre;

    /** Type d'évaluation */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEvalEnum type;

    /** L'utilisateur qui a passé cette évaluation */
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
     @JsonIgnore
    private Utilisateur utilisateur;

    /** La formation à laquelle cette évaluation est rattachée */
    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    /** Date et heure du passage de l'évaluation */
    @Column(nullable = false)
    private LocalDateTime datePassage;

    /** Score obtenu (en %) */
    private Double score;

    /** Résultat de l'évaluation après correction */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultatEnum statut;

    /** Numéro de la tentative (1, 2, 3...) */
    @Column(nullable = false)
    private Integer tentativeNum = 1;

    @PrePersist
    public void prePersist() {
        this.datePassage = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = ResultatEnum.EN_ATTENTE;
        }
    }
}
