package no.nav.tag.tiltaksgjennomforing.metrikker;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.tag.tiltaksgjennomforing.TestData;
import no.nav.tag.tiltaksgjennomforing.avtale.NavIdent;
import no.nav.tag.tiltaksgjennomforing.avtale.events.AvtaleOpprettet;
import no.nav.tag.tiltaksgjennomforing.featuretoggles.FeatureToggleService;
import no.nav.tag.tiltaksgjennomforing.metrikker.MetrikkRegistrering;
import no.nav.tag.tiltaksgjennomforing.varsel.SmsVarselRepository;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.pilottilgang.TilgangUnderPilotering;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.*;

public class MetrikkRegistreringTest {

    @Rule
    public OutputCapture capture = new OutputCapture();
    private FeatureToggleService featureToggleService = mock(FeatureToggleService.class);
    private MetrikkRegistrering metrikkRegistrering;

    @Before
    public void setUp() throws Exception {
      //  metrikkRegistrering = new MetrikkRegistrering(mock(MeterRegistry.class, RETURNS_DEEP_STUBS), mock(SmsVarselRepository.class), featureToggleService);
    }

    @Test
    public void opprettAvtale__pilottilgang_gjennom_unleash__skal_logge_pilotfylke() {
        when(featureToggleService.isEnabled(TilgangUnderPilotering.TAG_TILTAK_PILOTTILGANG_KONTOR)).thenReturn(true);
        metrikkRegistrering.avtaleOpprettet(new AvtaleOpprettet(TestData.enAvtale(), new NavIdent("X123459")));
        assertThat(capture.toString()).contains("PilotFylke=true");
    }

    @Test
    public void opprettAvtale__pilottilgang_gjennom_unleash__skal_ikke_logge_pilotfylke() {
        when(featureToggleService.isEnabled(TilgangUnderPilotering.TAG_TILTAK_PILOTTILGANG_KONTOR)).thenReturn(false);
        metrikkRegistrering.avtaleOpprettet(new AvtaleOpprettet(TestData.enAvtale(), new NavIdent("X123456")));
        assertThat(capture.toString()).contains("PilotFylke=false");
    }

}
