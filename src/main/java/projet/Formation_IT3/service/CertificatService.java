package  projet.Formation_IT3.service;

import  projet.Formation_IT3.modeles.Certificat;
import  projet.Formation_IT3.modeles.Formation;
import  projet.Formation_IT3.modeles.Utilisateur;
import  projet.Formation_IT3.repository.CertificatRepository;
import  projet.Formation_IT3.repository.FormationRepository;
import  projet.Formation_IT3.repository.UtilisateurRepository;
import  projet.Formation_IT3.util.CertificatIdGenerateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service gérant la génération et la délivrance des certificats de formation.
 * Un certificat est créé automatiquement après réussite de l'examen final.
 */
@Service
public class CertificatService {

    @Autowired
    private CertificatRepository certificatRepo;

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private FormationRepository formationRepo;

    @Autowired
    private CertificatIdGenerateur idGenerateur;

    /**
     * Génère et délivre un certificat à un apprenant.
     * Vérifie qu'aucun certificat n'existe déjà pour cette formation.
     *
     * @param utilisateurId ID de l'apprenant
     * @param formationId ID de la formation
     * @param scoreFinal Score obtenu à l'examen final
     * @return Le certificat généré avec son identifiant unique
     */
    public Certificat generer(Long utilisateurId, Long formationId, Double scoreFinal) {
        // Vérification : un seul certificat par formation et par utilisateur
        if (certificatRepo.existsByUtilisateurIdAndFormationId(utilisateurId, formationId)) {
            // Si le certificat existe déjà, on le retourne sans en créer un nouveau
            return certificatRepo.findByUtilisateurIdAndFormationId(utilisateurId, formationId)
                .orElseThrow();
        }

        Utilisateur utilisateur = utilisateurRepo.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + utilisateurId));

        Formation formation = formationRepo.findById(formationId)
            .orElseThrow(() -> new RuntimeException("Formation introuvable : " + formationId));

        // Génération d'un identifiant unique pour ce certificat (ex: KA-4821)
        String idUnique = idGenerateur.generer();

        Certificat certificat = new Certificat();
        certificat.setIdentifiantUnique(idUnique);
        certificat.setUtilisateur(utilisateur);
        certificat.setFormation(formation);
        certificat.setScoreFinal(scoreFinal);
        // URL simulée du PDF (en production : appel au service de génération PDF)
        certificat.setUrlPdf("/certificats/" + idUnique + ".pdf");

        return certificatRepo.save(certificat);
    }

    /** Tous les certificats obtenus par un utilisateur */
    public List<Certificat> getMesCertificats(Long utilisateurId) {
        return certificatRepo.findByUtilisateurId(utilisateurId);
    }

    /** Récupère un certificat par son identifiant unique public (KA-XXXX) */
    public Certificat getParIdentifiantUnique(String identifiantUnique) {
        return certificatRepo.findByIdentifiantUnique(identifiantUnique)
            .orElseThrow(() -> new RuntimeException(
                "Certificat introuvable avec l'identifiant : " + identifiantUnique
            ));
    }

    /** Récupère un certificat par son ID interne */
    public Certificat getParId(Long id) {
        return certificatRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Certificat introuvable : " + id));
    }
}
