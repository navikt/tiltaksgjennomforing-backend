package no.nav.tag.tiltaksgjennomforing.varsel;

import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.AvtaleRepository;
import no.nav.tag.tiltaksgjennomforing.TestData;
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
@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:smsVarselRepositoryTest"})
@ActiveProfiles("dev")
@DirtiesContext
public class SmsVarselRepositoryTest {
    @Autowired
    private SmsVarselRepository repository;
    @Autowired
    private AvtaleRepository avtaleRepository;
    private Avtale avtale;

    @Before
    public void setUp() {
        avtale = TestData.enAvtale();
        avtaleRepository.save(avtale);
    }

    @Test
    public void save__lagrer_riktig() {
        SmsVarsel smsVarsel = TestData.etSmsVarsel(avtale);
        SmsVarsel lagretSmsVarsel = repository.save(smsVarsel);
        assertThat(lagretSmsVarsel).isEqualToIgnoringNullFields(smsVarsel);
    }

    @Test
    public void antallUsendteSmsVarsler__teller_riktig() {
        repository.deleteAll();
        assertThat(repository.antallUsendte()).isEqualTo(0);
        Avtale avtale = TestData.enAvtale();
        avtale.settIdOgOpprettetTidspunkt();
        SmsVarsel smsVarsel = TestData.etSmsVarsel(avtale);
        repository.save(smsVarsel);
        assertThat(repository.antallUsendte()).isEqualTo(1);
    }
}