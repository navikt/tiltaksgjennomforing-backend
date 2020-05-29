package no.nav.tag.tiltaksgjennomforing.autorisasjon.altinntilgangsstyring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tiltaksgjennomforing.altinn-tilgangsstyring")
public class AltinnTilgangsstyringProperties {
    private String proxyUrl;
    private String proxyFallbackUrl;
    private String altinnApiKey;
    private String altinnHeader;
    private String APIGwHeader;
    private String apiGwApiKey;

    private Integer ltsMidlertidigServiceCode;
    private Integer ltsMidlertidigServiceEdition;
    private Integer ltsVarigServiceCode;
    private Integer ltsVarigServiceEdition;
    private Integer arbtreningServiceCode;
    private Integer arbtreningServiceEdition;
}
