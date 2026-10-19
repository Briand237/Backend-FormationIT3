package projet.Formation_IT3.util;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Utilitaire pour la génération d'identifiants uniques de certificats KAOLO.
 * Format : KA-XXXX où XXXX est un nombre aléatoire entre 1000 et 9999.
 */
@Component
public class CertificatIdGenerateur {

    private final Random random = new Random();

    /**
     * Génère un identifiant unique pour un certificat.
     * Exemple : KA-4821, KA-7392
     *
     * @return L'identifiant au format KA-XXXX
     */
    public String generer() {
        // Génération d'un nombre à 4 chiffres (1000-9999)
        int numero = 1000 + random.nextInt(9000);
        return "KA-" + numero;
    }
}
