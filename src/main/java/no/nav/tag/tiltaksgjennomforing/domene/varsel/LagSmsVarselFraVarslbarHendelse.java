package no.nav.tag.tiltaksgjennomforing.domene.varsel;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.domene.Avtale;
import no.nav.tag.tiltaksgjennomforing.domene.events.VarslbarHendelseOppstaatt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LagSmsVarselFraVarslbarHendelse {
    private final SmsVarselRepository smsVarselRepository;

    static List<SmsVarsel> lagSmsVarsler(Avtale avtale, VarslbarHendelse hendelse, GamleVerdier gamleVerdier) {
        SmsVarselFactory factory = new SmsVarselFactory(avtale, hendelse);
        switch (hendelse.getVarslbarHendelseType()) {
            case OPPRETTET:
                break;
            case GODKJENT_AV_DELTAKER:
            case GODKJENT_AV_ARBEIDSGIVER:
                return Arrays.asList(factory.veileder());
            case GODKJENT_AV_VEILEDER:
                return Arrays.asList(factory.deltaker(), factory.arbeidsgiver());
            case GODKJENT_PAA_VEGNE_AV:
                return Arrays.asList(factory.arbeidsgiver());
            case GODKJENNINGER_OPPHEVET_AV_ARBEIDSGIVER: {
                var varslinger = new ArrayList<SmsVarsel>();
                if (gamleVerdier.isGodkjentAvDeltaker()) {
                    varslinger.add(factory.deltaker());
                }
                varslinger.add(factory.veileder());
                return varslinger;
            }
            case GODKJENNINGER_OPPHEVET_AV_VEILEDER: {
                var varslinger = new ArrayList<SmsVarsel>();
                if (gamleVerdier.isGodkjentAvDeltaker()) {
                    varslinger.add(factory.deltaker());
                }
                if (gamleVerdier.isGodkjentAvArbeidsgiver()) {
                    varslinger.add(factory.arbeidsgiver());
                }
                return varslinger;
            }
        }
        return Collections.emptyList();
    }

    @EventListener
    public void lagreSmsVarsler(VarslbarHendelseOppstaatt event) {
        smsVarselRepository.saveAll(lagSmsVarsler(event.getAvtale(), event.getVarslbarHendelse(), event.getGamleVerdier()));
    }
}
