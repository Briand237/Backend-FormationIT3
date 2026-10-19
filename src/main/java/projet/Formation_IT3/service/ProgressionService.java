package projet.Formation_IT3.service;


import  projet.Formation_IT3.dtos.ProgressionRequestDto;
import  projet.Formation_IT3.modeles.Lecon;
import  projet.Formation_IT3.modeles.Progression;
import  projet.Formation_IT3.modeles.Utilisateur;
import  projet.Formation_IT3.repository.LeconRepository;
import  projet.Formation_IT3.repository.ProgressionRepository;
import  projet.Formation_IT3.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service gérant le suivi de la progression des apprenants leçon par leçon.
 * Sauvegarde la position vidéo et le statut de completion de chaque leçon.
 */
@Service
public class ProgressionService {

    @Autowired
    private ProgressionRepository progressionRepo;

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    @Autowired
    private LeconRepository leconRepo;

    /**
     * Crée ou met à jour la progression d'un utilisateur pour une leçon.
     * Si une progression existe déjà, elle est mise à jour (upsert).
     *
     * @param utilisateurId ID de l'apprenant
     * @param dto Les données de progression à enregistrer
     * @return La progression sauvegardée
     */
    public Progression sauvegarder(Long utilisateurId, ProgressionRequestDto dto) {
        Lecon lecon = leconRepo.findById(dto.getLeconId())
            .orElseThrow(() -> new RuntimeException("Leçon introuvable : " + dto.getLeconId()));

        Utilisateur utilisateur = utilisateurRepo.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + utilisateurId));

        // Recherche d'une progression existante pour cet utilisateur et cette leçon
        Progression progression = progressionRepo
            .findByUtilisateurIdAndLeconId(utilisateurId, dto.getLeconId())
            .orElse(new Progression()); // Création si inexistante

        // Mise à jour des données de progression
        progression.setUtilisateur(utilisateur);
        progression.setLecon(lecon);
        progression.setPositionVideo(dto.getPositionVideo());

        // Si marqué comme complète et pas encore complétée : enregistrer la date
        if (dto.isEstComplete() && !progression.isEstComplete()) {
            progression.setEstComplete(true);
            progression.setDateCompletion(LocalDateTime.now());
        }

        return progressionRepo.save(progression);
    }

    /** Récupère toutes les progressions d'un utilisateur */
    public List<Progression> getMesProgressions(Long utilisateurId) {
        return progressionRepo.findByUtilisateurId(utilisateurId);
    }

    /**
     * Calcule le pourcentage de progression global d'un utilisateur dans une formation.
     * Formule : (leçons complétées / total leçons) * 100
     *
     * @param utilisateurId ID de l'apprenant
     * @param formationId ID de la formation
     * @param totalLecons Nombre total de leçons dans la formation
     * @return Le pourcentage de complétion (0.0 à 100.0)
     */
    public double calculerPourcentage(Long utilisateurId, Long formationId, long totalLecons) {
        if (totalLecons == 0) return 0.0;
        long leconsCompletees = progressionRepo.countLeconsCompletees(utilisateurId, formationId);
        return ((double) leconsCompletees / totalLecons) * 100.0;
    }
}
