package no.nav.tag.tiltaksgjennomforing.integrasjon;

import no.nav.tag.tiltaksgjennomforing.domene.NavEnhet;
import no.nav.tag.tiltaksgjennomforing.domene.NavIdent;
import no.nav.tag.tiltaksgjennomforing.integrasjon.axsys.AxsysService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext
public class AxsysServiceTest {
    @Autowired
    private AxsysService axsysService;

    @Test
    public void hentEnheter__returnerer__riktig__enhet() {
        List<NavEnhet> enheter = axsysService.hentEnheterVeilederHarTilgangTil(new NavIdent("X123456"));
        assertThat(enheter).containsOnly(new NavEnhet("0906"), new NavEnhet("0904"));
    }

    @Test
    public void hentEnheter__ugyldig__ident__skal__ikke__ha__enheter() {
        List<NavEnhet> enheter = axsysService.hentEnheterVeilederHarTilgangTil(new NavIdent("X999999"));
        assertThat(enheter).isEmpty();
    }

    @Test
    public void pilotEnheter__inneholder__hentetEnheter() {
        List<NavEnhet> enheter = axsysService.hentEnheterVeilederHarTilgangTil(new NavIdent("X123456"));
        List<NavEnhet> pilotEnheter = asList(new NavEnhet("0906"));
        assertThat(pilotEnheter).containsAnyElementsOf(enheter);
    }

}
