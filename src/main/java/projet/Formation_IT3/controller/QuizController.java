package projet.Formation_IT3.controller;

import projet.Formation_IT3.dtos.ApiResponseDto;
import projet.Formation_IT3.dtos.QuestionEmployeDto;
import projet.Formation_IT3.dtos.ResultatQuizDto;
import projet.Formation_IT3.dtos.SoumissionQuizRequestDto;
import projet.Formation_IT3.modeles.Utilisateur;
import projet.Formation_IT3.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour le PASSAGE des quiz/examens côté EMPLOYÉ.
 *
 * Différence essentielle avec QuestionController : ici, les questions
 * renvoyées NE CONTIENNENT JAMAIS les bonnes réponses (voir QuestionEmployeDto).
 * Le détail des bonnes réponses n'est révélé qu'APRÈS soumission, dans
 * le ResultatQuizDto retourné par /soumettre.
 *
 * Routes :
 * GET  /api/quiz/module/{moduleId}         → Questions d'un quiz de module (sans réponses)
 * GET  /api/quiz/examen-final/{formationId} → Questions de l'examen final (sans réponses)
 * POST /api/quiz/soumettre                  → Soumet les réponses, reçoit le résultat corrigé
 */
@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "http://localhost:4200")
public class QuizController {

    @Autowired
    private QuizService quizService;

    private Utilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Utilisateur) auth.getPrincipal();
    }

    /** Récupère les questions du quiz d'un module, SANS les bonnes réponses */
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<ApiResponseDto<List<QuestionEmployeDto>>> getQuizModule(
            @PathVariable Long moduleId) {
        List<QuestionEmployeDto> questions = quizService.getQuestionsQuizModule(moduleId);
        return ResponseEntity.ok(ApiResponseDto.succes(
            questions.size() + " question(s)", questions));
    }

    /** Récupère les questions de l'examen final, SANS les bonnes réponses */
    @GetMapping("/examen-final/{formationId}")
    public ResponseEntity<ApiResponseDto<List<QuestionEmployeDto>>> getExamenFinal(
            @PathVariable Long formationId) {
        List<QuestionEmployeDto> questions = quizService.getQuestionsExamenFinal(formationId);
        return ResponseEntity.ok(ApiResponseDto.succes(
            questions.size() + " question(s) d'examen final", questions));
    }

    /**
     * Soumet les réponses de l'employé. Le score est calculé entièrement
     * côté serveur — impossible de le falsifier depuis le frontend.
     */
    @PostMapping("/soumettre")
    public ResponseEntity<ApiResponseDto<ResultatQuizDto>> soumettre(
            @Valid @RequestBody SoumissionQuizRequestDto dto) {
        try {
            Utilisateur utilisateur = getUtilisateurConnecte();
            ResultatQuizDto resultat = quizService.corriger(utilisateur.getId(), dto);

            String message = resultat.getStatut().name().equals("REUSSI")
                ? "Félicitations ! Score obtenu : " + resultat.getScore() + "%"
                : "Score insuffisant : " + resultat.getScore() + "%. Vous pouvez retenter.";

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.succes(message, resultat));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }
}
