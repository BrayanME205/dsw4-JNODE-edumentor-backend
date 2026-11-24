package cr.ac.ucr.edumentorhub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.edumentorhub.dto.evaluacion.EvaluacionRequest;
import cr.ac.ucr.edumentorhub.dto.evaluacion.EvaluacionResponse;
import cr.ac.ucr.edumentorhub.service.EvaluacionTutoriaService;
import jakarta.validation.Valid;

@RestController
public class EvaluacionTutoriaController {

    private final EvaluacionTutoriaService evaluacionService;

    public EvaluacionTutoriaController(EvaluacionTutoriaService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @PostMapping("/api/sesiones/{id}/evaluacion")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public EvaluacionResponse registrar(@PathVariable Long id, @Valid @RequestBody EvaluacionRequest request,
            Authentication authentication) {
        return evaluacionService.registrar(id, authentication.getName(), request);
    }

    @GetMapping("/api/sesiones/{id}/evaluacion")
    public EvaluacionResponse obtener(@PathVariable Long id, Authentication authentication) {
        return evaluacionService.obtener(id, authentication.getName());
    }

    /** Público (GET /api/tutores/** está abierto en SecurityConfig): reseñas para el perfil del tutor. */
    @GetMapping("/api/tutores/{tutorId}/evaluaciones")
    public List<EvaluacionResponse> listarPorTutor(@PathVariable Long tutorId) {
        return evaluacionService.listarPorTutor(tutorId);
    }
}
