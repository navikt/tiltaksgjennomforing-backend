package no.nav.tag.tiltaksgjennomforing.featuretoggles;

import no.finn.unleash.Unleash;
import no.finn.unleash.UnleashContext;
import no.finn.unleash.Variant;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.TokenUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeatureToggleServiceTest {

    @Mock private Unleash unleash;
    @Mock private TokenUtils innloggingService;

    @InjectMocks
    private FeatureToggleService featureToggleService;

    @Test
    public void hentFeatureToggles__skal_returnere_true_hvis_feature_er_på() {
        when(unleash.getVariant(eq("feature_som_er_på"), any(UnleashContext.class))).thenReturn(new Variant("disabled", (String) null, true));
        Map<String, Variant> toggles = featureToggleService.hentFeatureToggles(Arrays.asList("feature_som_er_på"));
        assertThat(toggles.get("feature_som_er_på").isEnabled()).isTrue();
    }

    @Test
    public void hentFeatureToggles__skal_returnere_false_hvis_feature_er_av() {
        when(unleash.getVariant(eq("feature_som_er_av"), any(UnleashContext.class))).thenReturn(new Variant("disabled", (String) null, false));
        Map<String, Variant> toggles = featureToggleService.hentFeatureToggles(Arrays.asList("feature_som_er_av"));
        assertThat(toggles.get("feature_som_er_av").isEnabled()).isFalse();
    }

    @Test
    public void hentFeatureToggles__skal_kunne_returnere_flere_toggles() {
        List<String> features = Arrays.asList("feature1", "feature2", "feature3");
        when(unleash.getVariant(eq("feature1"), any(UnleashContext.class))).thenReturn(new Variant("disabled", (String) null, true));
        when(unleash.getVariant(eq("feature2"), any(UnleashContext.class))).thenReturn(new Variant("disabled", (String) null, false));
        when(unleash.getVariant(eq("feature3"), any(UnleashContext.class))).thenReturn(new Variant("disabled", (String) null, false));

        Map<String, Variant> toggles = featureToggleService.hentFeatureToggles(features);

        assertThat(toggles.get("feature1").isEnabled()).isTrue();
        assertThat(toggles.get("feature2").isEnabled()).isFalse();
        assertThat(toggles.get("feature3").isEnabled()).isFalse();
        assertThat(toggles.size()).isEqualTo(3);
    }
}