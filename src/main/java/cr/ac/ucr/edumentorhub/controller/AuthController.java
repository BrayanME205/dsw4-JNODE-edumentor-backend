package cr.ac.ucr.edumentorhub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.edumentorhub.dto.auth.AuthResponse;
import cr.ac.ucr.edumentorhub.dto.auth.LoginRequest;
import cr.ac.ucr.edumentorhub.dto.auth.RegistroRequest;
import cr.ac.ucr.edumentorhub.dto.usuario.UsuarioResponse;
import cr.ac.ucr.edumentorhub.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest request) {
        return authService.registrar(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
