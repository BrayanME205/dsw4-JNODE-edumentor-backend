package cr.ac.ucr.edumentorhub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.edumentorhub.dto.materia.MateriaRequest;
import cr.ac.ucr.edumentorhub.dto.materia.MateriaResponse;
import cr.ac.ucr.edumentorhub.service.MateriaService;
import jakarta.validation.Valid;

/** Consultar es público (catálogo); crear, editar y eliminar es solo del COORDINADOR. */
@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @GetMapping
    public List<MateriaResponse> listar() {
        return materiaService.listar();
    }

    @GetMapping("/{id}")
    public MateriaResponse obtener(@PathVariable Long id) {
        return materiaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('COORDINADOR')")
    public MateriaResponse crear(@Valid @RequestBody MateriaRequest request) {
        return materiaService.crear(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COORDINADOR')")
    public MateriaResponse actualizar(@PathVariable Long id, @Valid @RequestBody MateriaRequest request) {
        return materiaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('COORDINADOR')")
    public void eliminar(@PathVariable Long id) {
        materiaService.eliminar(id);
    }
}
