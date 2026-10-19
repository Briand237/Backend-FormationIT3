package projet.Formation_IT3.service;

import  projet.Formation_IT3.enums.StatutEnum;
import  projet.Formation_IT3.modeles.Formation;
import  projet.Formation_IT3.modeles.Inscription;
import  projet.Formation_IT3.modeles.Utilisateur;
import  projet.Formation_IT3.repository.FormationRepository;
import  projet.Formation_IT3.repository.InscriptionRepository;
import  projet.Formation_IT3.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service gérant les inscriptions des employés aux formations.
 * Vérifie les doublons et maintient le statut de progression.
 */
@Service
public class InscriptionService {

    @Autowired
    private InscriptionRepository inscriptionRepo;

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private FormationRepository formationRepo;

    /**
     * Inscrit un employé à une formation.
     * Lève une exception si l'employé est déjà inscrit à cette formation.
     */
    public Inscription inscrire(Long utilisateurId, Long formationId) {
        // Vérification de doublon d'inscription
        if (inscriptionRepo.existsByUtilisateurIdAndFormationId(utilisateurId, formationId)) {
            throw new RuntimeException("Cet utilisateur est déjà inscrit à cette formation");
        }

        Utilisateur utilisateur = utilisateurRepo.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + utilisateurId));

        Formation formation = formationRepo.findById(formationId)
            .orElseThrow(() -> new RuntimeException("Formation introuvable : " + formationId));

        Inscription inscription = new Inscription();
        inscription.setUtilisateur(utilisateur);
        inscription.setFormation(formation);
        inscription.setStatut(StatutEnum.INSCRIT);
        inscription.setProgressionGlobale(0.0);

        return inscriptionRepo.save(inscription);
    }

    /** Récupère toutes les inscriptions d'un utilisateur */
    public List<Inscription> getMesInscriptions(Long utilisateurId) {
        return inscriptionRepo.findByUtilisateurId(utilisateurId);
    }

    /** Récupère une inscription spécifique */
    public Inscription getParId(Long id) {
        return inscriptionRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Inscription introuvable : " + id));
    }

    /**
     * Met à jour le statut et la progression globale d'une inscription.
     * Appelé automatiquement lors de chaque mise à jour de progression de leçon.
     */
    public Inscription mettreAJourProgression(Long utilisateurId, Long formationId,
                                               Double nouvelleProgression) {
        Inscription inscription = inscriptionRepo
            .findByUtilisateurIdAndFormationId(utilisateurId, formationId)
            .orElseThrow(() -> new RuntimeException("Inscription introuvable"));

        inscription.setProgressionGlobale(nouvelleProgression);

        // Mise à jour automatique du statut selon la progression
        if (nouvelleProgression > 0 && inscription.getStatut() == StatutEnum.INSCRIT) {
            inscription.setStatut(StatutEnum.EN_COURS);
        } else if (nouvelleProgression >= 100.0) {
            inscription.setStatut(StatutEnum.TERMINE);
        }

        return inscriptionRepo.save(inscription);
    }

    /** Désinscrire un utilisateur d'une formation */
    public void desinscrire(Long id) {
        inscriptionRepo.delete(getParId(id));
    }
}
