package cr.ac.ucr.edumentorhub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.edumentorhub.dto.recurso.RecursoRequest;
import cr.ac.ucr.edumentorhub.dto.recurso.RecursoResponse;
import cr.ac.ucr.edumentorhub.service.RecursoEstudioService;
import jakarta.validation.Valid;

/** Recursos de una sesión. El servicio verifica que quien llama sea participante. */
@RestController
@RequestMapping("/api/sesiones/{sesionId}/recursos")
public class RecursoEstudioController {

    private final RecursoEstudioService recursoService;

    public RecursoEstudioController(RecursoEstudioService recursoService) {
        this.recursoService = recursoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecursoResponse agregar(@PathVariable Long sesionId, @Valid @RequestBody RecursoRequest request,
            Authentication authentication) {
        return recursoService.agregar(sesionId, authentication.getName(), request);
    }

    @GetMapping
    public List<RecursoResponse> listar(@PathVariable Long sesionId, Authentication authentication) {
        return recursoService.listar(sesionId, authentication.getName());
    }
}
