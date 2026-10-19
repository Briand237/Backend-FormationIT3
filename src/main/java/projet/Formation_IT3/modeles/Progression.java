package  projet.Formation_IT3.modeles;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entité de suivi de la progression d'un apprenant pour une leçon donnée.
 * Permet de sauvegarder la position vidéo et de marquer une leçon comme complète.
 */
@Entity
@Table(
    name = "progressions",
    uniqueConstraints = {
        // Un seul enregistrement de progression par utilisateur et par leçon
        @UniqueConstraint(columnNames = {"utilisateur_id", "lecon_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Progression {

    /** Identifiant unique de la progression */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** L'utilisateur dont on suit la progression */
    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @JsonIgnore
    private Utilisateur utilisateur;

    /** La leçon concernée */
    @ManyToOne
    @JoinColumn(name = "lecon_id", nullable = false)
    private Lecon lecon;

    /** Indique si la leçon a été entièrement complétée */
    @Column(nullable = false)
    private boolean estComplete = false;

    /** Position actuelle dans la vidéo (en secondes) pour reprendre */
    private Integer positionVideo = 0;

    /** Date à laquelle la leçon a été complétée (null si pas encore) */
    private LocalDateTime dateCompletion;

    /** Date de la dernière mise à jour de la progression */
    private LocalDateTime dernierAcces;

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.dernierAcces = LocalDateTime.now();
    }
}
