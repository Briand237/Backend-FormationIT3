package projet.Formation_IT3.service;

import  projet.Formation_IT3.dtos.FormationRequestDto;
import  projet.Formation_IT3.modeles.Formation;
import  projet.Formation_IT3.modeles.Utilisateur;
import  projet.Formation_IT3.repository.FormationRepository;
import  projet.Formation_IT3.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service gérant toutes les opérations CRUD sur les formations.
 * Logique métier : validation, association au formateur, publication.
 */
@Service
public class FormationService {

    @Autowired
    private FormationRepository formationRepo;

    @Autowired
    private UtilisateurRepository utilisateurRepo;

    // ==================== CRÉATION ====================

    /**
     * Crée une nouvelle formation dans le catalogue.
     *
     * @param dto Les données de la formation à créer
     * @return La formation sauvegardée avec son ID généré
     */
    public Formation creer(FormationRequestDto dto) {
        Formation formation = new Formation();
        formation.setTitre(dto.getTitre());
        formation.setDescription(dto.getDescription());
        formation.setDomaine(dto.getDomaine());
        formation.setNiveau(dto.getNiveau());
        formation.setDureeHeures(dto.getDureeHeures());
        formation.setScoreMinCertification(dto.getScoreMinCertification());
        formation.setImageUrl(dto.getImageUrl());
        formation.setPubliee(dto.isPubliee());

        // Association du formateur si un ID est fourni
        if (dto.getFormateurId() != null) {
            Utilisateur formateur = utilisateurRepo.findById(dto.getFormateurId())
                .orElseThrow(() -> new RuntimeException(
                    "Formateur introuvable avec l'ID : " + dto.getFormateurId()
                ));
            formation.setFormateur(formateur);
        }

        return formationRepo.save(formation);
    }

    // ==================== LECTURE ====================

    /**
     * Récupère toutes les formations disponibles dans le catalogue (publiées).
     */
    public List<Formation> getToutesPubliees() {
        return formationRepo.findByPublieeTrue();
    }

    /**
     * Récupère toutes les formations (admin - publiées et non publiées).
     */
    public List<Formation> getToutesAdmin() {
        return formationRepo.findAll();
    }

    /**
     * Récupère une formation par son ID.
     *
     * @param id L'identifiant de la formation
     * @return La formation trouvée
     * @throws RuntimeException si la formation n'existe pas
     */
    public Formation getParId(Long id) {
        return formationRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Formation introuvable avec l'ID : " + id));
    }

    /**
     * Recherche des formations par domaine technique.
     */
    public List<Formation> getParDomaine(String domaine) {
        return formationRepo.findByDomaineAndPublieeTrue(domaine);
    }

    /**
     * Recherche des formations par mot-clé dans le titre.
     */
    public List<Formation> rechercherParMotCle(String motCle) {
        return formationRepo.findByTitreContainingIgnoreCaseAndPublieeTrue(motCle);
    }

    // ==================== MISE À JOUR ====================

    /**
     * Met à jour les informations d'une formation existante.
     *
     * @param id L'ID de la formation à modifier
     * @param dto Les nouvelles données
     * @return La formation mise à jour
     */
    public Formation modifier(Long id, FormationRequestDto dto) {
        // On vérifie que la formation existe avant de la modifier
        Formation formation = getParId(id);

        // Mise à jour des champs modifiables
        formation.setTitre(dto.getTitre());
        formation.setDescription(dto.getDescription());
        formation.setDomaine(dto.getDomaine());
        formation.setNiveau(dto.getNiveau());
        formation.setDureeHeures(dto.getDureeHeures());
        formation.setScoreMinCertification(dto.getScoreMinCertification());
        formation.setImageUrl(dto.getImageUrl());
        formation.setPubliee(dto.isPubliee());

        // Mise à jour du formateur si un nouvel ID est fourni
        if (dto.getFormateurId() != null) {
            Utilisateur formateur = utilisateurRepo.findById(dto.getFormateurId())
                .orElseThrow(() -> new RuntimeException(
                    "Formateur introuvable avec l'ID : " + dto.getFormateurId()
                ));
            formation.setFormateur(formateur);
        }

        return formationRepo.save(formation);
    }

    /**
     * Publie ou dépublie une formation du catalogue.
     *
     * @param id L'ID de la formation
     * @param publiee true pour publier, false pour dépublier
     */
    public Formation changerStatutPublication(Long id, boolean publiee) {
        Formation formation = getParId(id);
        formation.setPubliee(publiee);
        return formationRepo.save(formation);
    }

    // ==================== SUPPRESSION ====================

    /**
     * Supprime définitivement une formation et tous ses modules/leçons associés.
     * La suppression en cascade est gérée par JPA (CascadeType.ALL).
     *
     * @param id L'ID de la formation à supprimer
     */
    public void supprimer(Long id) {
        // Vérification que la formation existe avant suppression
        Formation formation = getParId(id);
        formationRepo.delete(formation);
    }
}
