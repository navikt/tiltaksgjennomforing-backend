package no.nav.tag.tiltaksgjennomforing.enhet;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
@ConfigurationProperties(prefix = "tiltaksgjennomforing.veilarbarena")
public class VeilarbArenaProperties {
    private URI url;
}
