package  projet.Formation_IT3.modeles;

import  projet.Formation_IT3.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entité représentant un utilisateur (employé) de la plateforme KAOLO.
 * Implémente UserDetails pour l'intégration avec Spring Security.
 */
@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur implements UserDetails {

    /** Identifiant unique généré automatiquement */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom complet de l'employé */
    @Column(nullable = false)
    private String nomComplet;

    /** Email professionnel - utilisé comme identifiant de connexion */
    @Column(nullable = false, unique = true)
    private String email;

    /** Mot de passe hashé avec BCrypt */
    @Column(nullable = false)
    @JsonIgnore
    private String motDePasse;

    /** Identifiant employé KAOLO (format KA-XXXX) */
    @Column(nullable = false, unique = true)
    private String idEmploye;

    /** Département de l'employé */
    private String departement;

    /** Rôle de l'utilisateur sur la plateforme */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEnum role;

    /** Date de création du compte */
    @Column(nullable = false)
    private LocalDateTime dateCreation;

    /** Indique si le compte est actif */
    @Column(nullable = false)
    private boolean actif = true;

    /** Initialise la date de création avant la première persistance */
    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        if (this.role == null) {
            this.role = RoleEnum.EMPLOYE;
        }
    }

    // --- Méthodes Spring Security ---

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // On transforme le rôle en GrantedAuthority pour Spring Security
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        // L'email est utilisé comme nom d'utilisateur
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() { return true; }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() { return actif; }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    @JsonIgnore
    public boolean isEnabled() { return actif; }
}
