package no.nav.tag.tiltaksgjennomforing.infrastruktur.sts;

import java.net.URI;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import no.nav.tag.tiltaksgjennomforing.infrastruktur.restservicecache.CacheConfiguration;

@Component
public class STSClient {

    private final RestTemplate stsBasicAuthRestTemplate;
    private final URI stsUri;
    public STSClient(StsProperties stsProperties) {
        this.stsBasicAuthRestTemplate = new RestTemplateBuilder()
                .basicAuthentication(stsProperties.getUsername(), stsProperties.getPassword())
                .build();
        this.stsUri = stsProperties.getRestUri();
    }

    @Cacheable(CacheConfiguration.STS_CACHE)
    public STSToken hentSTSToken() {
        String uriString = UriComponentsBuilder.fromUri(stsUri)
                .queryParam("grant_type", "client_credentials")
                .queryParam("scope", "openid")
                .toUriString();

        return stsBasicAuthRestTemplate.exchange(
                uriString,
                HttpMethod.GET,
                getRequestEntity(),
                STSToken.class
        ).getBody();

    }

    private HttpEntity<String> getRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(headers);
    }

    @CacheEvict(CacheConfiguration.STS_CACHE)
    public void evictToken() {
    }

}
