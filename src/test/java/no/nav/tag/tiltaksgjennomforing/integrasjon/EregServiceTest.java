package no.nav.tag.tiltaksgjennomforing.integrasjon;

import no.nav.tag.tiltaksgjennomforing.domene.BedriftNr;
import no.nav.tag.tiltaksgjennomforing.domene.Organisasjon;
import no.nav.tag.tiltaksgjennomforing.domene.exceptions.EnhetFinnesIkkeException;
import no.nav.tag.tiltaksgjennomforing.integrasjon.ereg.EregService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"dev", "wiremock"})
@DirtiesContext
public class EregServiceTest {
    @Autowired
    private EregService eregService;

    @Test
    public void hentBedriftNavn__returnerer_navn_og_bedriftnr() {
        Organisasjon organisasjon = eregService.hentVirksomhet(new BedriftNr("999999999"));
        assertThat(organisasjon.getBedriftNr()).isEqualTo(new BedriftNr("999999999"));
        assertThat(organisasjon.getBedriftNavn()).isEqualTo("Saltrød og Høneby");
    }

    @Test(expected = EnhetFinnesIkkeException.class)
    public void hentBedriftNavn__kaster_exception_ved_404() {
        eregService.hentVirksomhet(new BedriftNr("899999999"));
    }
}