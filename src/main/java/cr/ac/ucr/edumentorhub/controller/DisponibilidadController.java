package cr.ac.ucr.edumentorhub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.edumentorhub.dto.disponibilidad.DisponibilidadRequest;
import cr.ac.ucr.edumentorhub.dto.disponibilidad.DisponibilidadResponse;
import cr.ac.ucr.edumentorhub.service.DisponibilidadTutorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/disponibilidades")
public class DisponibilidadController {

    private final DisponibilidadTutorService disponibilidadService;

    public DisponibilidadController(DisponibilidadTutorService disponibilidadService) {
        this.disponibilidadService = disponibilidadService;
    }

    /** Horario semanal de un tutor. Es público para mostrarlo en su perfil. */
    @GetMapping
    public List<DisponibilidadResponse> listar(@RequestParam Long tutorId) {
        return disponibilidadService.listarPorTutor(tutorId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('TUTOR')")
    public DisponibilidadResponse crear(@Valid @RequestBody DisponibilidadRequest request,
            Authentication authentication) {
        return disponibilidadService.crear(authentication.getName(), request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TUTOR')")
    public DisponibilidadResponse actualizar(@PathVariable Long id, @Valid @RequestBody DisponibilidadRequest request,
            Authentication authentication) {
        return disponibilidadService.actualizar(id, authentication.getName(), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('TUTOR')")
    public void desactivar(@PathVariable Long id, Authentication authentication) {
        disponibilidadService.desactivar(id, authentication.getName());
    }
}
