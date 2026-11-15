package cr.ac.ucr.edumentorhub.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cr.ac.ucr.edumentorhub.exception.ProblemDetailWriter;
import cr.ac.ucr.edumentorhub.security.CustomUserDetailsService;
import cr.ac.ucr.edumentorhub.security.JwtAuthenticationFilter;
import cr.ac.ucr.edumentorhub.security.JwtService;

/**
 * Reglas de seguridad de la API:
 * - Sin sesión en servidor (STATELESS): cada petición trae su token JWT.
 * - Rutas públicas: auth, consola H2 y la consulta (GET) de catálogos para la parte pública del frontend.
 * - Todo lo demás exige token; los permisos por rol se piden con @PreAuthorize en los controladores.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final String origenPermitido;

    public SecurityConfig(JwtService jwtService, CustomUserDetailsService userDetailsService,
            @Value("${app.cors.allowed-origin}") String origenPermitido) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.origenPermitido = origenPermitido;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // la consola H2 se muestra dentro de un iframe del mismo origen
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(antMatcher("/api/auth/**")).permitAll()
                .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                .requestMatchers(antMatcher("/error")).permitAll()
                .requestMatchers(antMatcher(HttpMethod.GET, "/api/materias/**")).permitAll()
                .requestMatchers(antMatcher(HttpMethod.GET, "/api/tutores/**")).permitAll()
                .requestMatchers(antMatcher(HttpMethod.GET, "/api/disponibilidades/**")).permitAll()
                .anyRequest().authenticated())
            // los errores de los filtros no pasan por @RestControllerAdvice: se escriben aquí en RFC 7807
            .exceptionHandling(errores -> errores
                .authenticationEntryPoint((request, response, ex) -> ProblemDetailWriter.escribir(
                        request, response, 401, "No autenticado",
                        "Debe iniciar sesión y enviar un token válido."))
                .accessDeniedHandler((request, response, ex) -> ProblemDetailWriter.escribir(
                        request, response, 403, "Acceso denegado",
                        "Su rol no tiene permiso para realizar esta operación.")))
            .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Permite que el frontend Angular (http://localhost:4200) llame a la API desde el navegador. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(origenPermitido));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", config);
        return fuente;
    }
}
