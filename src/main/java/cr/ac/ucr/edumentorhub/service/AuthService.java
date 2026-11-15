package cr.ac.ucr.edumentorhub.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.edumentorhub.dto.auth.AuthResponse;
import cr.ac.ucr.edumentorhub.dto.auth.LoginRequest;
import cr.ac.ucr.edumentorhub.dto.auth.RegistroRequest;
import cr.ac.ucr.edumentorhub.dto.usuario.UsuarioResponse;
import cr.ac.ucr.edumentorhub.entity.Rol;
import cr.ac.ucr.edumentorhub.entity.Usuario;
import cr.ac.ucr.edumentorhub.exception.ConflictoException;
import cr.ac.ucr.edumentorhub.exception.ReglaNegocioException;
import cr.ac.ucr.edumentorhub.mapper.UsuarioMapper;
import cr.ac.ucr.edumentorhub.repository.UsuarioRepository;
import cr.ac.ucr.edumentorhub.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        // El coordinador no se autoregistra: se crea con los datos semilla (REQ-21)
        if (request.rol() == Rol.COORDINADOR) {
            throw new ReglaNegocioException("Solo es posible registrarse como ESTUDIANTE o TUTOR");
        }
        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictoException("Ya existe una cuenta con el correo " + email);
        }
        if (usuarioRepository.existsByCarne(request.carne())) {
            throw new ConflictoException("Ya existe una cuenta con el carné " + request.carne());
        }

        Usuario usuario = new Usuario(request.carne(), email, passwordEncoder.encode(request.password()),
                request.nombre(), request.apellido(), request.rol());
        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        // Si el correo o la contraseña no coinciden, lanza BadCredentialsException (401)
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        return new AuthResponse(jwtService.generarToken(usuario), "Bearer", usuario.getId(),
                usuario.getNombre(), usuario.getRol());
    }
}
