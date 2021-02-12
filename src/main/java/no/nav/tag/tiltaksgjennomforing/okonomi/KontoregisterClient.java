package no.nav.tag.tiltaksgjennomforing.okonomi;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.exceptions.KontoregisterFeilException;
import no.nav.tag.tiltaksgjennomforing.infrastruktur.CorrelationIdSupplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class KontoregisterClient {

    private final String url;
    private final RestTemplate restTemplate;

    //TODO: Hent fra properties object
    public KontoregisterClient(@Value("${tiltaksgjennomforing.kontoregister.uri}") String url, @Qualifier("azure") RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public String hentKontonummer(String bedriftNr) {
        try {
            KontoregisterResponse response = restTemplate.getForObject(String.format("%s/%s", url,bedriftNr),  KontoregisterResponse.class);

            if (response != null && response.getFeilmelding() != null) {
                log.error("Kontoregister svarte med feil for bedrift : " + bedriftNr, response.getFeilmelding());
                throw new KontoregisterFeilException();
            }

            return response.getKontonr();

        } catch (RestClientException exception) {
            log.error(String.format("Feil fra kontoregister med request-url: %s/%s", url,bedriftNr), exception);
            throw new KontoregisterFeilException();
        }
    }

    private HttpEntity lagRequest(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Nav-consumer-Id", "tiltaksgjennomforing-api");
        headers.set("Nav-Call-Id", CorrelationIdSupplier.get());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(headers);
    }
}
