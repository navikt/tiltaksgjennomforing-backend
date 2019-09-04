package no.nav.tag.tiltaksgjennomforing.integrasjon.altinn_varsel;

import lombok.RequiredArgsConstructor;
import no.altinn.services.serviceengine.notification._2010._10.INotificationAgencyExternalBasic;
import no.nav.tag.tiltaksgjennomforing.integrasjon.WsClient;
import no.nav.tag.tiltaksgjennomforing.integrasjon.configurationProperties.AltinnVarselProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile({"dev"})
public class AltinnVarselConfiguration {
    private final AltinnVarselProperties varselProperties;

    @Bean
    public INotificationAgencyExternalBasic iNotificationAgencyExternalBasic() {
        return new WsClient<INotificationAgencyExternalBasic>().createPort(varselProperties.getUri().toString(),
                INotificationAgencyExternalBasic.class,
                true);
    }
}
