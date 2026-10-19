package projet.Formation_IT3.service;

import  projet.Formation_IT3.modeles.Utilisateur;
import  projet.Formation_IT3.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service implémentant UserDetailsService pour que Spring Security puisse
 * charger les informations d'un utilisateur depuis la base de données KAOLO.
 */
@Service
public class UtilisateurDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    /**
     * Charge un utilisateur par son email pour l'authentification Spring Security.
     *
     * @param email L'email de l'utilisateur (utilisé comme identifiant de connexion)
     * @return Les détails de l'utilisateur pour Spring Security
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé avec cet email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return utilisateurRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Aucun utilisateur trouvé avec l'email : " + email
            ));
    }
}
