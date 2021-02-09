package no.nav.tag.tiltaksgjennomforing.varsel;

import no.nav.tag.tiltaksgjennomforing.Miljø;
import no.nav.tag.tiltaksgjennomforing.avtale.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(Miljø.LOCAL)
@DirtiesContext
public class VarslbarHendelseTest {

    @Autowired
    VarselRepository varselRepository;
    @Autowired
    AvtaleRepository avtaleRepository;

    @Test
    public void sjekk_at_alle_avslagsgrunner_og_forklaring_er_i_varseltekst() {
        EnumSet<Avslagsårsak> avslagsårsaker = EnumSet.of(Avslagsårsak.FEIL_I_PROSENTSATS, Avslagsårsak.FEIL_I_FAKTA, Avslagsårsak.FEIL_I_REGELFORSTÅELSE);
        String avslagsforklaring = "Masse feil";

        Avtale avtale = TestData.enLonnstilskuddAvtaleGodkjentAvVeileder();
        avtale.avslåTilskuddsperiode(TestData.enNavIdent(), avslagsårsaker, avslagsforklaring);
        avtaleRepository.save(avtale);

        List<Varsel> varsler = varselRepository.findAllByAvtaleIdAndIdentifikator(avtale.getId(), avtale.getVeilederNavIdent());

        String varselTekst = varsler.stream().filter(elem -> elem.getHendelseType() == VarslbarHendelseType.TILSKUDDSPERIODE_AVSLATT).findFirst().orElseThrow().getTekst();
        assertThat(varselTekst).contains(Avslagsårsak.FEIL_I_PROSENTSATS.getTekst().toLowerCase(), Avslagsårsak.FEIL_I_FAKTA.getTekst().toLowerCase(), Avslagsårsak.FEIL_I_REGELFORSTÅELSE.getTekst().toLowerCase());
        assertThat(varselTekst).contains(avslagsforklaring);
    }
}
