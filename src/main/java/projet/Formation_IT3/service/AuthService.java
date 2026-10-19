package  projet.Formation_IT3.service;

import  projet.Formation_IT3.dtos.AuthResponseDto;
import  projet.Formation_IT3.dtos.InscriptionRequestDto;
import  projet.Formation_IT3.jwt.JwtUtil;
import  projet.Formation_IT3.dtos.ConnexionRequestDto;
import  projet.Formation_IT3.modeles.Utilisateur;
import  projet.Formation_IT3.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service gérant l'authentification des utilisateurs KAOLO.
 * Gère l'inscription, la connexion et la génération de tokens JWT.
 */
@Service
public class AuthService {

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Inscrit un nouvel employé sur la plateforme.
     * Vérifie l'unicité de l'email et de l'ID employé avant la création.
     *
     * @param dto Les données d'inscription validées
     * @return La réponse avec le token JWT et les infos utilisateur
     * @throws RuntimeException si l'email ou l'ID employé est déjà utilisé
     */
    public AuthResponseDto inscrire(InscriptionRequestDto dto) {

        // Vérification de l'unicité de l'email
        if (utilisateurRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé : " + dto.getEmail());
        }

        // Vérification de l'unicité de l'ID employé
        if (utilisateurRepo.existsByIdEmploye(dto.getIdEmploye())) {
            throw new RuntimeException("Cet ID employé est déjà enregistré : " + dto.getIdEmploye());
        }

        // Construction de l'entité Utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNomComplet(dto.getNomComplet());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setIdEmploye(dto.getIdEmploye());
        utilisateur.setDepartement(dto.getDepartement());
        // Hachage du mot de passe avant stockage (BCrypt)
        utilisateur.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));

        // Sauvegarde en base de données
        Utilisateur sauvegarde = utilisateurRepo.save(utilisateur);

        // Génération du token JWT pour connexion immédiate après inscription
        String token = jwtUtil.genererToken(sauvegarde);
        

        return new AuthResponseDto(
            token,
            sauvegarde.getId(),
            sauvegarde.getNomComplet(),
            sauvegarde.getEmail(),
            sauvegarde.getRole()
        );
    }

    /**
     * Authentifie un utilisateur et retourne un token JWT.
     *
     * @param dto Les identifiants de connexion
     * @return La réponse avec le token JWT valide
     * @throws org.springframework.security.core.AuthenticationException si les identifiants sont invalides
     */
    public AuthResponseDto connecter(ConnexionRequestDto dto) {

        // Délégation de l'authentification à Spring Security
        // Lance une exception automatiquement si les identifiants sont incorrects
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getMotDePasse())
        );

        // Récupération de l'utilisateur authentifié
        Utilisateur utilisateur = (Utilisateur) auth.getPrincipal();

        // Génération du token JWT
        String token = jwtUtil.genererToken(utilisateur);

        return new AuthResponseDto(
            token,
            utilisateur.getId(),
            utilisateur.getNomComplet(),
            utilisateur.getEmail(),
            utilisateur.getRole()
        );
    }
}
