package cr.ac.ucr.edumentorhub.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Reloj del sistema como bean. Los servicios usan LocalDateTime.now(clock) en lugar de LocalDateTime.now(),
 * así las pruebas unitarias pueden fijar la hora con Clock.fixed(...).
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
