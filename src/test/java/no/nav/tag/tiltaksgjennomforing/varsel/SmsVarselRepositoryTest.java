package no.nav.tag.tiltaksgjennomforing.varsel;

import no.nav.tag.tiltaksgjennomforing.TestData;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.AvtaleRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

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
    @Autowired
    private VarslbarHendelseRepository varslbarHendelseRepository;
    private Avtale avtale;
    private VarslbarHendelse varslbarHendelse;
    @Autowired
    private EntityManager entityManager;

    @Before
    public void setUp() {
        avtale = TestData.enArbeidstreningAvtale();
        avtaleRepository.save(avtale);
        varslbarHendelse = TestData.enHendelse(avtale);
        varslbarHendelseRepository.save(varslbarHendelse);
    }

    @Test
    public void save__lagrer_riktig() {
        SmsVarsel smsVarsel = SmsVarsel.nyttVarsel("00000000", TestData.enIdentifikator(), "mld", varslbarHendelse.getId());
        SmsVarsel lagretSmsVarsel = repository.save(smsVarsel);
        assertThat(lagretSmsVarsel).isEqualToIgnoringNullFields(smsVarsel);
    }
}