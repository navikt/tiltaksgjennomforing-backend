package no.nav.tag.tiltaksgjennomforing.metrikker;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.tag.tiltaksgjennomforing.TestData;
import no.nav.tag.tiltaksgjennomforing.avtale.NavIdent;
import no.nav.tag.tiltaksgjennomforing.avtale.events.AvtaleOpprettet;
import no.nav.tag.tiltaksgjennomforing.metrikker.MetrikkRegistrering;
import no.nav.tag.tiltaksgjennomforing.varsel.SmsVarselRepository;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.pilottilgang.PilotProperties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

public class MetrikkRegistreringTest {

    @Rule
    public OutputCapture capture = new OutputCapture();
    private PilotProperties pilotProperties;
    private MetrikkRegistrering metrikkRegistrering;

    @Before
    public void setUp() throws Exception {
        pilotProperties = new PilotProperties();
        pilotProperties.setIdenter(List.of(new NavIdent("X123456")));
        metrikkRegistrering = new MetrikkRegistrering(mock(MeterRegistry.class, RETURNS_DEEP_STUBS), pilotProperties, mock(SmsVarselRepository.class));
    }

    @Test
    public void opprettAvtale__skal_logge_pilotfylke() {
        metrikkRegistrering.avtaleOpprettet(new AvtaleOpprettet(TestData.enAvtale(), new NavIdent("X123459")));
        assertThat(capture.toString()).contains("PilotFylke=true");
    }

    @Test
    public void opprettAvtale__skal_ikke_logge_pilotfylke() {
        metrikkRegistrering.avtaleOpprettet(new AvtaleOpprettet(TestData.enAvtale(), new NavIdent("X123456")));
        assertThat(capture.toString()).contains("PilotFylke=false");
    }
}