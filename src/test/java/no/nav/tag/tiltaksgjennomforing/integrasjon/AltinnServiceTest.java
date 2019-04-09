package no.nav.tag.tiltaksgjennomforing.integrasjon;

import no.nav.tag.tiltaksgjennomforing.domene.BedriftNr;
import no.nav.tag.tiltaksgjennomforing.domene.Fnr;
import no.nav.tag.tiltaksgjennomforing.domene.Organisasjon;
import no.nav.tag.tiltaksgjennomforing.domene.TestData;
import no.nav.tag.tiltaksgjennomforing.integrasjon.configurationProperties.AltinnProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext
public class AltinnServiceTest {
    @Autowired
    private AltinnService altinnService;

    @Test
    public void hentOrganisasjoner__gyldig_fnr_en_bedrift() {
        List<Organisasjon> organisasjoner = altinnService.hentOrganisasjoner(new Fnr("10000000000"));
        assertThat(organisasjoner).extracting("bedriftNr").containsOnly(new BedriftNr("610909086"));
    }

    @Test
    public void hentOrganisasjoner__gyldig_fnr_tom_liste() {
        List<Organisasjon> organisasjoner = altinnService.hentOrganisasjoner(new Fnr("00000000000"));
        assertThat(organisasjoner).hasSize(0);
    }

    @Test
    public void hentOrganisasjoner__ugyldig_fnr_tom_liste() {
        List<Organisasjon> organisasjoner = altinnService.hentOrganisasjoner(TestData.enIdentifikator());
        assertThat(organisasjoner).hasSize(0);
    }

    @Test
    public void hentOrganisasjoner__feilkonfigurasjon_tom_liste() {
        AltinnProperties altinnProperties = new AltinnProperties();
        altinnProperties.setAltinnUri(URI.create("http://foobar"));
        List<Organisasjon> organisasjoner = new AltinnService(altinnProperties).hentOrganisasjoner(TestData.enIdentifikator());
        assertThat(organisasjoner).hasSize(0);
    }
}