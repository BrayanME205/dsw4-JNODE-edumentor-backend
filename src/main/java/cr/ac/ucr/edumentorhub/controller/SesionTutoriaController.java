package cr.ac.ucr.edumentorhub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.edumentorhub.dto.sesion.SesionResponse;
import cr.ac.ucr.edumentorhub.dto.sesion.SolicitudSesionRequest;
import cr.ac.ucr.edumentorhub.service.SesionTutoriaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sesiones")
public class SesionTutoriaController {

    private final SesionTutoriaService sesionService;

    public SesionTutoriaController(SesionTutoriaService sesionService) {
        this.sesionService = sesionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public SesionResponse solicitar(@Valid @RequestBody SolicitudSesionRequest request, Authentication authentication) {
        return sesionService.solicitar(authentication.getName(), request);
    }

    @GetMapping("/mias")
    public List<SesionResponse> listarMias(Authentication authentication) {
        return sesionService.listarMias(authentication.getName());
    }

    @GetMapping("/{id}")
    public SesionResponse obtener(@PathVariable Long id, Authentication authentication) {
        return sesionService.obtener(id, authentication.getName());
    }

    @PutMapping("/{id}/aceptar")
    @PreAuthorize("hasRole('TUTOR')")
    public SesionResponse aceptar(@PathVariable Long id, Authentication authentication) {
        return sesionService.aceptar(id, authentication.getName());
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('TUTOR')")
    public SesionResponse rechazar(@PathVariable Long id, Authentication authentication) {
        return sesionService.rechazar(id, authentication.getName());
    }
}
