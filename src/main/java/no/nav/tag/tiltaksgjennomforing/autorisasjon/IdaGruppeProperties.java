package no.nav.tag.tiltaksgjennomforing.autorisasjon;

import java.util.UUID;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tiltaksgjennomforing.beslutter-ad-gruppe")
public class IdaGruppeProperties {

    private UUID id;
}
