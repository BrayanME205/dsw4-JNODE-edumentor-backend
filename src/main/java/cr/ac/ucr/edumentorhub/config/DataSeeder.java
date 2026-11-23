package cr.ac.ucr.edumentorhub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.edumentorhub.entity.Materia;
import cr.ac.ucr.edumentorhub.entity.Rol;
import cr.ac.ucr.edumentorhub.entity.Usuario;
import cr.ac.ucr.edumentorhub.repository.MateriaRepository;
import cr.ac.ucr.edumentorhub.repository.UsuarioRepository;

/**
 * Datos de demostración que se crean al arrancar la aplicación (no en las pruebas).
 * Es idempotente: si un usuario o una materia ya existe, no se vuelve a crear.
 * Contraseña común de todas las cuentas de prueba: Test1234!
 */
@Component
@Profile("!test")
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final String CLAVE_PRUEBA = "Test1234!";

    private final UsuarioRepository usuarioRepository;
    private final MateriaRepository materiaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository, MateriaRepository materiaRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.materiaRepository = materiaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        crearUsuario("A00001", "coordinador@ucr.ac.cr", "Carmen", "Solano", Rol.COORDINADOR);
        crearUsuario("B10001", "tutor1@ucr.ac.cr", "Luis", "Rojas", Rol.TUTOR);
        crearUsuario("B10002", "tutor2@ucr.ac.cr", "María", "Vega", Rol.TUTOR);
        crearUsuario("C20001", "estudiante1@ucr.ac.cr", "Ana", "Mora", Rol.ESTUDIANTE);
        crearUsuario("C20002", "estudiante2@ucr.ac.cr", "Diego", "Brenes", Rol.ESTUDIANTE);

        crearMateria("IF0001", "Programación I", "Informática Empresarial", 4);
        crearMateria("IF0005", "Bases de Datos I", "Informática Empresarial", 4);
        crearMateria("IF0009", "Desarrollo de Software IV", "Informática Empresarial", 4);
        crearMateria("MA0001", "Cálculo I", "Ingeniería", 4);
        crearMateria("FS0001", "Física General I", "Ingeniería", 3);

        log.info("Datos semilla listos (contraseña de prueba: {})", CLAVE_PRUEBA);
    }

    private void crearUsuario(String carne, String email, String nombre, String apellido, Rol rol) {
        if (!usuarioRepository.existsByEmail(email)) {
            usuarioRepository.save(new Usuario(carne, email, passwordEncoder.encode(CLAVE_PRUEBA),
                    nombre, apellido, rol));
        }
    }

    private void crearMateria(String codigo, String nombre, String carrera, int creditos) {
        if (!materiaRepository.existsByCodigoMateria(codigo)) {
            materiaRepository.save(new Materia(codigo, nombre, carrera, creditos));
        }
    }
}
