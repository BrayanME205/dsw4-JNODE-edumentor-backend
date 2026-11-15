package cr.ac.ucr.edumentorhub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import cr.ac.ucr.edumentorhub.dto.auth.RegistroRequest;
import cr.ac.ucr.edumentorhub.dto.usuario.UsuarioResponse;
import cr.ac.ucr.edumentorhub.entity.Rol;
import cr.ac.ucr.edumentorhub.entity.Usuario;
import cr.ac.ucr.edumentorhub.exception.ConflictoException;
import cr.ac.ucr.edumentorhub.exception.ReglaNegocioException;
import cr.ac.ucr.edumentorhub.repository.UsuarioRepository;
import cr.ac.ucr.edumentorhub.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegistroRequest solicitud(Rol rol) {
        return new RegistroRequest("C10001", "Ana@UCR.ac.cr", "Test1234!", "Ana", "Mora", rol);
    }

    @Test
    void registrarConEmailDuplicadoLanzaConflicto() {
        when(usuarioRepository.existsByEmail("ana@ucr.ac.cr")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> authService.registrar(solicitud(Rol.ESTUDIANTE)));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrarComoCoordinadorNoEstaPermitido() {
        assertThrows(ReglaNegocioException.class, () -> authService.registrar(solicitud(Rol.COORDINADOR)));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrarGuardaLaContrasenaCifradaYElCorreoEnMinuscula() {
        when(usuarioRepository.existsByEmail("ana@ucr.ac.cr")).thenReturn(false);
        when(usuarioRepository.existsByCarne("C10001")).thenReturn(false);
        when(passwordEncoder.encode("Test1234!")).thenReturn("hash-bcrypt");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse respuesta = authService.registrar(solicitud(Rol.TUTOR));

        assertEquals("ana@ucr.ac.cr", respuesta.email());
        assertEquals(Rol.TUTOR, respuesta.rol());
        verify(usuarioRepository).save(org.mockito.ArgumentMatchers.<Usuario>argThat(
                u -> u.getPasswordHash().equals("hash-bcrypt")));
    }
}
