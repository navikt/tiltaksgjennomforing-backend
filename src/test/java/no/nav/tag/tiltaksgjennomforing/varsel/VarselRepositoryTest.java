package no.nav.tag.tiltaksgjennomforing.varsel;

import no.nav.tag.tiltaksgjennomforing.Miljø;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.AvtaleRepository;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;
import no.nav.tag.tiltaksgjennomforing.avtale.TestData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(Miljø.LOCAL)
@DirtiesContext
public class VarselRepositoryTest {
    @Autowired
    private VarselRepository varselRepository;
    @Autowired
    private AvtaleRepository avtaleRepository;
    private Avtale avtale;

    @Before
    public void setUp() {
        avtale = TestData.enArbeidstreningAvtale();
        avtaleRepository.save(avtale);
    }

    @Test
    public void save__lagrer_riktig() {
        Varsel varsel = Varsel.nyttVarsel(TestData.enIdentifikator(), true, avtale, Avtalerolle.DELTAKER, Avtalerolle.VEILEDER, VarslbarHendelseType.ENDRET, avtale.getId());
        Varsel lagretVarsel = varselRepository.save(varsel);
        assertThat(lagretVarsel).isEqualToIgnoringNullFields(varsel);
    }
}
