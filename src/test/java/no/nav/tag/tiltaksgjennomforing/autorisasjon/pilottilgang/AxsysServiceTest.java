package no.nav.tag.tiltaksgjennomforing.autorisasjon.pilottilgang;

import no.nav.tag.tiltaksgjennomforing.avtale.NavIdent;

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
@ActiveProfiles({"dev", "wiremock"})
@DirtiesContext
public class AxsysServiceTest {
    @Autowired
    private AxsysService axsysService;

    @Test
    public void hentEnheter__returnerer_riktige_enheter() {
        List<NavEnhet> enheter = axsysService.hentEnheterVeilederHarTilgangTil(new NavIdent("X123456")).get();
        assertThat(enheter).containsOnly(new NavEnhet("0906"), new NavEnhet("0904"));
    }

    @Test
    public void hentEnheter__ugyldig_ident_skal_ikke_ha_enheter() {
        List<NavEnhet> enheter = axsysService.hentEnheterVeilederHarTilgangTil(new NavIdent("X999999")).get();
        assertThat(enheter).isEmpty();
    }

    @Test
    public void pilotEnheter__inneholder_hentetEnheter() {
        List<NavEnhet> enheter = axsysService.hentEnheterVeilederHarTilgangTil(new NavIdent("X123456")).get();
        List<NavEnhet> pilotEnheter = asList(new NavEnhet("0906"));
        assertThat(pilotEnheter).containsAnyElementsOf(enheter);
    }

}
