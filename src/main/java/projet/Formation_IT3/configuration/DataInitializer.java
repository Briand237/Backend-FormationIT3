
/*
package projet.Formation_IT3.configuration;

import projet.Formation_IT3.enums.RoleEnum;
import projet.Formation_IT3.modeles.Utilisateur;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(projet.Formation_IT3.repository.UtilisateurRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByEmail("admin@gmail.com").isEmpty()) {
                Utilisateur admin = new projet.Formation_IT3.modeles.Utilisateur();
                admin.setNomComplet("Super Admin Kambou");
                admin.setEmail("admin@gmail.com");
                admin.setIdEmploye("KA-0001");
                admin.setDepartement("IT");
                admin.setRole(RoleEnum.SUPER_ADMIN);
                admin.setMotDePasse(encoder.encode("Admin123"));
                admin.setActif(true);
                repo.save(admin);
                System.out.println(">>> ADMIN CREE : admin@gmail.com / Admin123");
            }
        };
    }
}

 */