package  projet.Formation_IT3.modeles;

import  projet.Formation_IT3.enums.TypeLeconEnum;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant une leçon au sein d'un module.
 * Une leçon est l'unité pédagogique atomique : vidéo, texte ou exercice.
 */
@Entity
@Table(name = "lecons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lecon {

    /** Identifiant unique de la leçon */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre de la leçon */
    @Column(nullable = false)
    private String titre;

    /** Type de contenu de la leçon */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeLeconEnum type;

    /** URL de la vidéo (si type = VIDEO) */
    private String urlVideo;

    /** Contenu textuel de la leçon (si type = TEXTE) */
    @Column(columnDefinition = "TEXT")
    private String contenuTexte;

    /** Durée de la leçon en secondes */
    private Integer dureeSecondes;

    /** Position de la leçon dans le module (commence à 1) */
    @Column(nullable = false)
    private Integer ordre;

    /** Le module auquel appartient cette leçon */
    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    @JsonIgnore
    private Module module;
}
