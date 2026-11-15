package cr.ac.ucr.edumentorhub.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import cr.ac.ucr.edumentorhub.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Genera y valida los tokens JWT (firma HS256 con la clave app.jwt.secret).
 * El "subject" del token es el correo; además lleva el id y el rol como claims.
 */
@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(@Value("${app.jwt.secret}") String secreto,
            @Value("${app.jwt.expiration-ms}") long expiracionMs) {
        // HS256 exige una clave de al menos 32 bytes
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMs;
    }

    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave)
                .compact();
    }

    public String extraerEmail(String token) {
        return leerClaims(token).getSubject();
    }

    public boolean esValido(String token, UserDetails usuario) {
        Claims claims = leerClaims(token);
        return claims.getSubject().equals(usuario.getUsername())
                && claims.getExpiration().after(new Date());
    }

    /** Verifica la firma; si el token fue alterado o venció, JJWT lanza JwtException. */
    private Claims leerClaims(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
