package projet.Formation_IT3.modeles;

import projet.Formation_IT3.enums.NiveauEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une formation disponible dans le catalogue KAOLO.
 * Une formation est composée de plusieurs modules et peut délivrer un certificat.
 */
@Entity
@Table(name = "formations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Formation {

    /** Identifiant unique de la formation */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre de la formation */
    @Column(nullable = false)
    private String titre;

    /** Description détaillée du contenu et des objectifs */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Domaine technique (Cloud & DevOps, Cybersécurité, etc.) */
    @Column(nullable = false)
    private String domaine;

    /** Niveau de difficulté */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauEnum niveau;

    /** Durée totale estimée en heures */
    @Column(nullable = false)
    private Integer dureeHeures;

    /** Score minimum requis pour obtenir le certificat (en %) */
    @Column(nullable = false)
    private Double scoreMinCertification = 80.0;

    /** URL de l'image de couverture de la formation */
    private String imageUrl;

    /** Formateur responsable de cette formation */
    @ManyToOne
    @JoinColumn(name = "formateur_id")
    private Utilisateur formateur;

    /** Liste des modules composant cette formation */
    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<Module> modules = new ArrayList<>();

    /** Indique si la formation est publiée dans le catalogue */
    @Column(nullable = false)
    private boolean publiee = false;

    /** Date de création de la formation */
    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
    }
}
