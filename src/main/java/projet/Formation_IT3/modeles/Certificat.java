package projet.Formation_IT3.modeles;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entité représentant un certificat délivré après réussite d'une formation.
 * Chaque certificat possède un identifiant unique vérifiable (format KA-XXXX).
 */
@Entity
@Table(name = "certificats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certificat {

    /** Identifiant unique interne */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant unique public du certificat (ex: KA-9821) */
    @Column(nullable = false, unique = true)
    private String identifiantUnique;

    /** L'utilisateur qui a obtenu ce certificat */
    @ManyToOne
     @JsonIgnore
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    /** La formation pour laquelle le certificat est délivré */
    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    /** Date d'obtention du certificat */
    @Column(nullable = false)
    private LocalDateTime dateObtention;

    /** URL du fichier PDF du certificat en stockage */
    private String urlPdf;

    /** Score final obtenu à l'examen de certification */
    private Double scoreFinal;

    @PrePersist
    public void prePersist() {
        this.dateObtention = LocalDateTime.now();
    }
}
