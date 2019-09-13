package no.nav.tag.tiltaksgjennomforing.featuretoggles;

import org.junit.Test;

import no.finn.unleash.UnleashContext;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.pilottilgang.NavEnhet;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.pilottilgang.AxsysService;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static no.nav.tag.tiltaksgjennomforing.featuretoggles.ByEnhetStrategy.PARAM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ByEnhetStrategyTest {

    private AxsysService axsysService = mock(AxsysService.class);
    private UnleashContext unleashContext = UnleashContext.builder().userId("brukerId").build();

    @Test
    public void skal_være_disablet_hvis_det_toggle_evalueres_uten_kontekst() {
        assertThat(new ByEnhetStrategy(axsysService).isEnabled(Map.of(PARAM, "1234"))).isEqualTo(false);
    }
    
    @Test
    public void skal_være_disablet_hvis_det_ikke_finnes_bruker_i_konteksten() {
        assertThat(new ByEnhetStrategy(axsysService).isEnabled(Map.of(PARAM, "1234"), UnleashContext.builder().build())).isEqualTo(false);
    }
    
    @Test
    public void skal_være_disablet_hvis_det_ikke_finnes_definerte_enheter() {
        assertThat(new ByEnhetStrategy(axsysService).isEnabled(emptyMap(), unleashContext)).isEqualTo(false);
        assertThat(new ByEnhetStrategy(axsysService).isEnabled(singletonMap(PARAM, null), unleashContext)).isEqualTo(false); //Map.of() tåler ikke null
        assertThat(new ByEnhetStrategy(axsysService).isEnabled(Map.of(PARAM, ""), unleashContext)).isEqualTo(false);
    }

    @Test
    public void skal_være_disablet_hvis_bruker_ikke_har_definerte_enheter() {
        assertThat(new ByEnhetStrategy(axsysService).isEnabled(Map.of(PARAM, "1234"), unleashContext)).isEqualTo(false);
    }

    @Test
    public void skal_være_disablet_hvis_bruker_har_definerte_enheter_men_ingen_er_i_listen() {
        when(axsysService.hentEnheterVeilederHarTilgangTil(any())).thenReturn(newArrayList(new NavEnhet("1111"), new NavEnhet("2222")));
        assertThat(new ByEnhetStrategy(axsysService).isEnabled(Map.of(PARAM, "1234"), unleashContext)).isEqualTo(false);
    }
    
    @Test
    public void skal_være_enablet_hvis_bruker_har_definert_enhet() {
        when(axsysService.hentEnheterVeilederHarTilgangTil(any())).thenReturn(newArrayList(new NavEnhet("1234")));
        assertThat(new ByEnhetStrategy(axsysService).isEnabled(Map.of(PARAM, "1234"), unleashContext)).isEqualTo(true);
    }

    @Test
    public void skal_være_enablet_hvis_en_av_brukers_enheter_er_i_listen() {
        when(axsysService.hentEnheterVeilederHarTilgangTil(any())).thenReturn(newArrayList(new NavEnhet("1111"), new NavEnhet("1234")));
        assertThat(new ByEnhetStrategy(axsysService).isEnabled(Map.of(PARAM, "1234,5678"), unleashContext)).isEqualTo(true);
    }
    
}
