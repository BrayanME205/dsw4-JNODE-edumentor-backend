package cr.ac.ucr.edumentorhub.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.edumentorhub.dto.tutor.TutorResponse;
import cr.ac.ucr.edumentorhub.service.TutorService;

/** Catálogo público de tutores con su calificación promedio. */
@RestController
@RequestMapping("/api/tutores")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping
    public List<TutorResponse> listar() {
        return tutorService.listar();
    }

    @GetMapping("/{id}")
    public TutorResponse obtener(@PathVariable Long id) {
        return tutorService.obtener(id);
    }
}
