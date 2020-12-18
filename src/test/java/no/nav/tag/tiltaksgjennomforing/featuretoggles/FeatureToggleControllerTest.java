package no.nav.tag.tiltaksgjennomforing.featuretoggles;

import no.finn.unleash.FakeUnleash;
import no.finn.unleash.Variant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeatureToggleControllerTest {

    @Mock private HttpServletResponse response;
    @Mock private FeatureToggleService featureToggleService;

    @InjectMocks private FeatureToggleController featureToggleController;

    @Test
    public void feature__skal_returnere_status_200_ved_get() {
        assertThat(featureToggleController.feature(Arrays.asList("darkMode", "nightMode")).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void feature__skal_returnere_respons_fra_service() {
        List<String> features = Arrays.asList("darkMode", "nightMode");
        Map<String, Variant> toggles = new HashMap<>(){{
            put("darkMode", new Variant("disabled", (String) null, true));
            put("nightMode", new Variant("disabled", (String) null, false));
        }};

        when(featureToggleService.hentFeatureToggles(eq(features))).thenReturn(toggles);

        Map<String, Variant> resultat = featureToggleController.feature(features).getBody();

        assertThat(resultat).isEqualTo(toggles);
    }
}
