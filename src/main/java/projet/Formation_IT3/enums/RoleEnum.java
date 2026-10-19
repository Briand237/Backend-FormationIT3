package projet.Formation_IT3.enums;

/**
 * Énumération des rôles disponibles dans la plateforme KAOLO.
 * Chaque rôle définit un niveau d'accès différent aux fonctionnalités.
 */
public enum RoleEnum {
    /** Employé standard - accès formation uniquement */
    EMPLOYE,
    /** Formateur - peut créer et gérer des formations */
    FORMATEUR,
    /** Manager - peut voir la progression de son équipe */
    MANAGER,
    /** Administrateur RH - gestion complète des utilisateurs et rapports */
    ADMIN,
    /** Super Administrateur - accès total au système */
    SUPER_ADMIN
}
