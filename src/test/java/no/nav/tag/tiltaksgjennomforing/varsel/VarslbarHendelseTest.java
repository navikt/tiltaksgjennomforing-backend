package no.nav.tag.tiltaksgjennomforing.varsel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;

import no.nav.tag.tiltaksgjennomforing.Miljø;
import no.nav.tag.tiltaksgjennomforing.avtale.Avslagsårsak;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.AvtaleRepository;
import no.nav.tag.tiltaksgjennomforing.avtale.TestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(Miljø.LOCAL)
@DirtiesContext
public class VarslbarHendelseTest {

    @Autowired
    VarselService varselService;
    @Autowired
    AvtaleRepository avtaleRepository;

    @Test
    public void sjekk_at_alle_avslagsgrunner_og_forklaring_er_i_varslbarhendelse() {
        EnumSet<Avslagsårsak> avslagsårsaker = EnumSet.of(Avslagsårsak.FEIL_I_PROSENTSATS, Avslagsårsak.FEIL_I_FAKTA, Avslagsårsak.FEIL_I_REGELFORSTÅELSE);
        String avslagsforklaring = "Masse feil";

        Avtale avtale = TestData.enLonnstilskuddAvtaleGodkjentAvVeileder();
        avtale.avslåTilskuddsperiode(TestData.enNavIdent(), avslagsårsaker, avslagsforklaring);
        avtaleRepository.save(avtale);

        List<Varsel> varsler = varselService.varslerForAvtale(TestData.enVeileder(avtale), avtale.getId());
        assertThat(varsler).extracting("varslbarHendelseType").contains(VarslbarHendelseType.TILSKUDDSPERIODE_AVSLATT);

        Varsel varselTekst = varsler.stream().filter(elem -> elem.getVarslbarHendelseType().equals(VarslbarHendelseType.TILSKUDDSPERIODE_AVSLATT)).collect(Collector.of(Varsel::new))
        assertThat(varselTekst).contains(Avslagsårsak.FEIL_I_PROSENTSATS, Avslagsårsak.FEIL_I_FAKTA.getTekst().toLowerCase(), Avslagsårsak.FEIL_I_REGELFORSTÅELSE.getTekst().toLowerCase());
        assertThat(varselTekst).contains(avslagsforklaring);
    }



}
