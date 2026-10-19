package projet.Formation_IT3.modeles;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entité représentant un module (chapitre) au sein d'une formation.
 * Un module regroupe plusieurs leçons thématiques ordonnées séquentiellement.
 */
@Entity
@Table(name = "modules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Module {

    /** Identifiant unique du module */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre du module */
    @Column(nullable = false)
    private String titre;

    /** Position du module dans la formation (commence à 1) */
    @Column(nullable = false)
    private Integer ordre;

    /** Durée estimée du module en minutes */
    private Integer dureeMinutes;

    /** La formation à laquelle appartient ce module */
    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
     @JsonIgnore
    private Formation formation;

    /** Liste des leçons contenues dans ce module */
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<Lecon> lecons = new ArrayList<>();
}
