package projet.Formation_IT3.controller;

import projet.Formation_IT3.dtos.ApiResponseDto;
import projet.Formation_IT3.dtos.QuestionRequestDto;
import projet.Formation_IT3.modeles.Question;
import projet.Formation_IT3.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la GESTION des questions de QCM (côté formateur/admin).
 * Toutes les routes ici exposent les bonnes réponses (estCorrecte) — réservé
 * strictement aux rôles FORMATEUR/ADMIN/SUPER_ADMIN.
 *
 * Pour la consultation côté EMPLOYÉ (sans les bonnes réponses), voir QuizController.
 *
 * Routes :
 * GET    /api/questions/module/{moduleId}           → Questions d'un quiz de module (vue formateur)
 * GET    /api/questions/examen-final/{formationId}   → Questions de l'examen final (vue formateur)
 * POST   /api/questions                              → Créer une question
 * PUT    /api/questions/{id}                          → Modifier une question
 * DELETE /api/questions/{id}                          → Supprimer une question
 */
@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasAnyRole('FORMATEUR', 'ADMIN', 'SUPER_ADMIN')")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<ApiResponseDto<List<Question>>> getParModule(@PathVariable Long moduleId) {
        List<Question> questions = questionService.getParModule(moduleId);
        return ResponseEntity.ok(ApiResponseDto.succes(
            questions.size() + " question(s) trouvée(s)", questions));
    }

    @GetMapping("/examen-final/{formationId}")
    public ResponseEntity<ApiResponseDto<List<Question>>> getExamenFinal(@PathVariable Long formationId) {
        List<Question> questions = questionService.getExamenFinal(formationId);
        return ResponseEntity.ok(ApiResponseDto.succes(
            questions.size() + " question(s) d'examen final", questions));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<Question>> creer(@Valid @RequestBody QuestionRequestDto dto) {
        try {
            Question question = questionService.creer(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.succes("Question créée avec succès", question));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Question>> modifier(
            @PathVariable Long id, @Valid @RequestBody QuestionRequestDto dto) {
        try {
            return ResponseEntity.ok(ApiResponseDto.succes(
                "Question mise à jour", questionService.modifier(id, dto)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> supprimer(@PathVariable Long id) {
        try {
            questionService.supprimer(id);
            return ResponseEntity.ok(ApiResponseDto.succes("Question supprimée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.erreur(e.getMessage()));
        }
    }
}
