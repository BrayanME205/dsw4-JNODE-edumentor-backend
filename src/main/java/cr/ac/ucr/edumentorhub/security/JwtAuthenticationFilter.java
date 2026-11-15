package cr.ac.ucr.edumentorhub.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Se ejecuta una vez por petición: si viene "Authorization: Bearer <token>" y el token es válido,
 * deja al usuario autenticado en el SecurityContext. Si no, la petición sigue sin autenticar
 * y, si la ruta es protegida, Spring Security responde 401.
 *
 * No es @Component a propósito: lo crea SecurityConfig para que no se registre dos veces.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String encabezado = request.getHeader("Authorization");
        if (encabezado == null || !encabezado.startsWith(PREFIJO)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = encabezado.substring(PREFIJO.length());
        try {
            String email = jwtService.extraerEmail(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails usuario = userDetailsService.loadUserByUsername(email);
                if (jwtService.esValido(token, usuario)) {
                    UsernamePasswordAuthenticationToken autenticacion =
                            new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                }
            }
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
            // Token alterado, vencido o de un usuario que ya no existe: se continúa sin autenticar.
        }

        filterChain.doFilter(request, response);
    }
}
