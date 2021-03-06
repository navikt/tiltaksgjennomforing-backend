package no.nav.tag.tiltaksgjennomforing.featuretoggles.enhet;

import no.nav.tag.tiltaksgjennomforing.Miljø;
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
@ActiveProfiles({ Miljø.LOCAL, "wiremock"})
@DirtiesContext
public class AxsysServiceTest {
    @Autowired
    private AxsysService axsysService;

    @Test
    public void hentEnheter__returnerer_riktige_enheter() {
        List<NavEnhet> enheter = axsysService.hentEnheterNavAnsattHarTilgangTil(new NavIdent("X123456"));
        assertThat(enheter).containsOnly(new NavEnhet("0906"), new NavEnhet("0904"));
    }

    @Test
    public void hentEnheter__ugyldig_ident_skal_ikke_ha_enheter() {
        List<NavEnhet> enheter = axsysService.hentEnheterNavAnsattHarTilgangTil(new NavIdent("X999999"));
        assertThat(enheter).isEmpty();
    }

    @Test
    public void pilotEnheter__inneholder_hentetEnheter() {
        List<NavEnhet> enheter = axsysService.hentEnheterNavAnsattHarTilgangTil(new NavIdent("X123456"));
        List<NavEnhet> pilotEnheter = asList(new NavEnhet("0906"));
        assertThat(pilotEnheter).containsAnyElementsOf(enheter);
    }

}
