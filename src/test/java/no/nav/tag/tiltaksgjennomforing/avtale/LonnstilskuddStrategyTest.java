package no.nav.tag.tiltaksgjennomforing.avtale;

import no.nav.tag.tiltaksgjennomforing.TestData;
import no.nav.tag.tiltaksgjennomforing.exceptions.AvtalensVarighetMerEnnMaksimaltAntallMånederException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static no.nav.tag.tiltaksgjennomforing.avtale.Tiltakstype.MIDLERTIDIG_LONNSTILSKUDD;
import static no.nav.tag.tiltaksgjennomforing.avtale.Tiltakstype.VARIG_LONNSTILSKUDD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LonnstilskuddStrategyTest {

    private AvtaleInnhold avtaleInnhold;
    private AvtaleInnholdStrategy strategy;

    @BeforeEach
    public void setUp(){
        avtaleInnhold = new AvtaleInnhold();
        strategy = AvtaleInnholdStrategyFactory.create(avtaleInnhold, MIDLERTIDIG_LONNSTILSKUDD);
    }

    @Test
    void test_at_feil_når_familietilknytning_ikke_er_fylt_ut() {
        EndreAvtale endreAvtale = TestData.endringPåAlleFelter();
        endreAvtale.setHarFamilietilknytning(true);
        endreAvtale.setFamilietilknytningForklaring(null);
        strategy.endre(endreAvtale);

        assertThat(strategy.erAltUtfylt()).isFalse();
    }

    @Test
    void test_at_ikke_feil_når_ikke_forklaring_og_nei_på_familietilknytning() {
        EndreAvtale endreAvtale = TestData.endringPåAlleFelter();
        endreAvtale.setHarFamilietilknytning(false);
        endreAvtale.setFamilietilknytningForklaring(null);
        strategy.endre(endreAvtale);

        assertThat(strategy.erAltUtfylt()).isTrue();
    }

    @Test
    void test_at_ikke_feil_når_alt_fylt_ut_og_har_familietilknytning() {
        EndreAvtale endreAvtale = TestData.endringPåAlleFelter();
        endreAvtale.setHarFamilietilknytning(true);
        endreAvtale.setFamilietilknytningForklaring("En god forklaring");
        strategy.endre(endreAvtale);

        assertThat(strategy.erAltUtfylt()).isTrue();
    }

    @Test
    void test_at_ingeting_er_utfylt_gir_false() {
        assertThat(strategy.erAltUtfylt()).isFalse();
    }

    @Test
    public void endreMidlertidigLønnstilskudd__startdato_og_sluttdato_satt_24mnd() {
        EndreAvtale endreAvtale = TestData.endringPåAlleFelter();
        LocalDate startDato = LocalDate.now();
        LocalDate sluttDato = startDato.plusMonths(24);
        endreAvtale.setStartDato(startDato);
        endreAvtale.setSluttDato(sluttDato);
        strategy.endre(endreAvtale);

        assertThat(strategy.erAltUtfylt()).isTrue();
    }


    @Test
    public void endreMidlertidigLønnstilskudd__startdato_og_sluttdato_satt_over_24mnd() {
        EndreAvtale endreAvtale = TestData.endringPåAlleFelter();
        LocalDate startDato = LocalDate.now();
        LocalDate sluttDato = startDato.plusMonths(24).plusDays(1);
        endreAvtale.setStartDato(startDato);
        endreAvtale.setSluttDato(sluttDato);
        assertThrows(AvtalensVarighetMerEnnMaksimaltAntallMånederException.class, () -> strategy.endre(endreAvtale));
    }

    @Test
    public void endreVarigLønnstilskudd__startdato_og_sluttdato_satt_over_24mnd() {
        strategy = AvtaleInnholdStrategyFactory.create(avtaleInnhold, VARIG_LONNSTILSKUDD);
        EndreAvtale endreAvtale = TestData.endringPåAlleFelter();
        LocalDate startDato = LocalDate.now();
        LocalDate sluttDato = startDato.plusMonths(24).plusDays(1);
        endreAvtale.setStartDato(startDato);
        endreAvtale.setSluttDato(sluttDato);
        strategy.endre(endreAvtale);

        assertThat(strategy.erAltUtfylt()).isTrue();
    }

}