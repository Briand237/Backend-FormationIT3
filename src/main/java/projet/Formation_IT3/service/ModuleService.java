package projet.Formation_IT3.service;

import  projet.Formation_IT3.dtos.ModuleRequestDto;
import  projet.Formation_IT3.modeles.Formation;
import  projet.Formation_IT3.modeles.Module;
import  projet.Formation_IT3.repository.FormationRepository;
import  projet.Formation_IT3.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service gérant les opérations CRUD sur les modules d'une formation.
 */
@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepo;

    @Autowired
    private FormationRepository formationRepo;

    /** Crée un nouveau module et l'associe à une formation */
    public Module creer(ModuleRequestDto dto) {
        Formation formation = formationRepo.findById(dto.getFormationId())
            .orElseThrow(() -> new RuntimeException("Formation introuvable : " + dto.getFormationId()));

        Module module = new Module();
        module.setTitre(dto.getTitre());
        module.setOrdre(dto.getOrdre());
        module.setDureeMinutes(dto.getDureeMinutes());
        module.setFormation(formation);

        return moduleRepo.save(module);
    }

    /** Récupère tous les modules d'une formation, triés par ordre */
    public List<Module> getParFormation(Long formationId) {
        return moduleRepo.findByFormationIdOrderByOrdreAsc(formationId);
    }

    /** Récupère un module par son ID */
    public Module getParId(Long id) {
        return moduleRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Module introuvable : " + id));
    }

    /** Met à jour un module existant */
    public Module modifier(Long id, ModuleRequestDto dto) {
        Module module = getParId(id);
        module.setTitre(dto.getTitre());
        module.setOrdre(dto.getOrdre());
        module.setDureeMinutes(dto.getDureeMinutes());
        return moduleRepo.save(module);
    }

    /** Supprime un module et toutes ses leçons (cascade) */
    public void supprimer(Long id) {
        moduleRepo.delete(getParId(id));
    }
}
