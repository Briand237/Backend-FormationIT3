package projet.Formation_IT3.modeles;

import projet.Formation_IT3.enums.StatutEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entité représentant l'inscription d'un employé à une formation.
 * Fait le lien entre un utilisateur et une formation, avec le suivi du statut.
 */
@Entity
@Table(
    name = "inscriptions",
    uniqueConstraints = {
        // Un employé ne peut s'inscrire qu'une seule fois à la même formation
        @UniqueConstraint(columnNames = {"utilisateur_id", "formation_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscription {

    /** Identifiant unique de l'inscription */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** L'utilisateur inscrit à la formation */
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @JsonIgnore
    private Utilisateur utilisateur;

    /** La formation concernée par cette inscription */
    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    /** Date à laquelle l'inscription a été créée */
    @Column(nullable = false)
    private LocalDateTime dateInscription;

    /** Statut actuel de l'inscription */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEnum statut;

    /** Pourcentage de progression global (0 à 100) */
    @Column(nullable = false)
    private Double progressionGlobale = 0.0;

    @PrePersist
    public void prePersist() {
        this.dateInscription = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = StatutEnum.INSCRIT;
        }
    }
}
