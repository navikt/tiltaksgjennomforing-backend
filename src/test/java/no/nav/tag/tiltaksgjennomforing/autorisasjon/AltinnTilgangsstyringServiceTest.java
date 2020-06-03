package no.nav.tag.tiltaksgjennomforing.autorisasjon;

import lombok.SneakyThrows;
import no.nav.tag.tiltaksgjennomforing.TestData;
import no.nav.tag.tiltaksgjennomforing.avtale.BedriftNr;
import no.nav.tag.tiltaksgjennomforing.avtale.Fnr;
import no.nav.tag.tiltaksgjennomforing.avtale.Tiltakstype;
import no.nav.tag.tiltaksgjennomforing.exceptions.TiltaksgjennomforingException;
import no.nav.tag.tiltaksgjennomforing.featuretoggles.FeatureToggleService;
import no.nav.tag.tiltaksgjennomforing.orgenhet.ArbeidsgiverOrganisasjon;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.altinntilgangsstyring.AltinnTilgangsstyringService;
import no.nav.tag.tiltaksgjennomforing.autorisasjon.altinntilgangsstyring.AltinnTilgangsstyringProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"dev", "wiremock"})
@DirtiesContext
public class AltinnTilgangsstyringServiceTest {
    @Autowired
    private AltinnTilgangsstyringService altinnTilgangsstyringService;

    @MockBean
    private TokenUtils tokenUtils;

    @MockBean
    private FeatureToggleService featureToggleService;

    @Before
    public void setUp() {
        when(tokenUtils.hentSelvbetjeningToken()).thenReturn("token");
        when(featureToggleService.isEnabled(anyString())).thenReturn(false);
    }

    @Test
    public void hentOrganisasjoner__gyldig_fnr_en_bedrift_pa_hvert_titlak() {
        List<ArbeidsgiverOrganisasjon> organisasjoner =
                altinnTilgangsstyringService.hentOrganisasjoner(new Fnr("10000000000"));

        assertThat(organisasjoner)
                .extracting("bedriftNr")
                .containsOnly(
                        new BedriftNr("999999999"),
                        new BedriftNr("910712314"),
                        new BedriftNr("910712306")
                );
    }

    @Test
    public void hentOrganisasjoner__fnr_skal_ha_tilgang_pa_arbeidstrening() {
        List<ArbeidsgiverOrganisasjon> organisasjoner =
                altinnTilgangsstyringService.hentOrganisasjoner(new Fnr("20000000000"));

        assertThat(organisasjoner)
                .flatExtracting(ArbeidsgiverOrganisasjon.Fields.tilgangstyper)
                .containsOnly(Tiltakstype.ARBEIDSTRENING);
    }

    @Test
    public void hentOrganisasjoner__fnr_skal_ha_tilgang_pa_midlertidig_lonnstilskudd() {
        List<ArbeidsgiverOrganisasjon> organisasjoner =
                altinnTilgangsstyringService.hentOrganisasjoner(new Fnr("26000000000"));

        assertThat(organisasjoner)
                .flatExtracting(ArbeidsgiverOrganisasjon.Fields.tilgangstyper)
                .containsOnly(Tiltakstype.MIDLERTIDIG_LONNSTILSKUDD);
    }

    @Test
    public void hentOrganisasjoner__fnr_skal_ha_tilgang_pa_varig_lonnstilskudd() {
        List<ArbeidsgiverOrganisasjon> organisasjoner =
                altinnTilgangsstyringService.hentOrganisasjoner(new Fnr("27000000000"));

        assertThat(organisasjoner)
                .flatExtracting(ArbeidsgiverOrganisasjon.Fields.tilgangstyper)
                .containsOnly(Tiltakstype.VARIG_LONNSTILSKUDD);
    }

    @Test
    public void hentOrganisasjoner__fnr_skal_ha_tilgang_pa_alle_tiltakstyper() {
        List<ArbeidsgiverOrganisasjon> organisasjoner =
                altinnTilgangsstyringService.hentOrganisasjoner(new Fnr("10000000000"));

        assertThat(organisasjoner)
                .flatExtracting(ArbeidsgiverOrganisasjon.Fields.tilgangstyper)
                .contains(
                        Tiltakstype.ARBEIDSTRENING,
                        Tiltakstype.MIDLERTIDIG_LONNSTILSKUDD,
                        Tiltakstype.VARIG_LONNSTILSKUDD
                );
    }


    @Test
    public void hentOrganisasjoner__gyldig_fnr_en_andre_bedrift() {
        List<ArbeidsgiverOrganisasjon> organisasjoner =
                altinnTilgangsstyringService.hentOrganisasjoner(new Fnr("20000000000"));

        assertThat(organisasjoner)
                .extracting("bedriftNr")
                .containsOnly(
                        new BedriftNr("981121465"),
                        new BedriftNr("910909088")
                );
    }

    @Test
    public void hentOrganisasjoner__gyldig_fnr_tom_liste() {
        List<ArbeidsgiverOrganisasjon> organisasjoner =
                altinnTilgangsstyringService.hentOrganisasjoner(new Fnr("00000000000"));

        assertThat(organisasjoner).hasSize(0);
    }

    @Test(expected = TiltaksgjennomforingException.class)
    public void hentOrganisasjoner__ugyldig_fnr_skal_kaste_feil() {
        altinnTilgangsstyringService.hentOrganisasjoner(TestData.enIdentifikator());
    }

    @SneakyThrows
    @Test (expected = TiltaksgjennomforingException.class)
    public void hentOrganisasjoner__feilkonfigurasjon_skal_kaste_feil() {
        AltinnTilgangsstyringProperties altinnTilgangsstyringProperties = new AltinnTilgangsstyringProperties();
        altinnTilgangsstyringProperties.setAltinnUrl(new URL("http://foobarbazz"));
        altinnTilgangsstyringProperties.setProxyUrl(new URL("http://foobar"));
        altinnTilgangsstyringProperties.setProxyFallbackUrl(new URL("http://foobarbazz"));
        altinnTilgangsstyringProperties.setAltinnApiKey("test");
        altinnTilgangsstyringProperties.setAltinnHeader("test");
        altinnTilgangsstyringProperties.setAPIGwHeader("test");
        altinnTilgangsstyringProperties.setApiGwApiKey("test");
        altinnTilgangsstyringProperties.setLtsMidlertidigServiceEdition(1111);
        altinnTilgangsstyringProperties.setLtsMidlertidigServiceCode(1);
        altinnTilgangsstyringProperties.setLtsVarigServiceEdition(3333);
        altinnTilgangsstyringProperties.setLtsVarigServiceCode(3);
        altinnTilgangsstyringProperties.setArbtreningServiceCode(2222);
        altinnTilgangsstyringProperties.setArbtreningServiceEdition(2);

        new AltinnTilgangsstyringService(
                altinnTilgangsstyringProperties,
                tokenUtils,
                featureToggleService
        ).hentOrganisasjoner(
                TestData.enIdentifikator()
        );
    }
}
