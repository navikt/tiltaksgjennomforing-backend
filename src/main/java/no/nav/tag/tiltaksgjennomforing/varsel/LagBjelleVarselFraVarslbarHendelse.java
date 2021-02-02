package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.events.GamleVerdier;
import no.nav.tag.tiltaksgjennomforing.varsel.events.VarslbarHendelseOppstaatt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LagBjelleVarselFraVarslbarHendelse {
    private final BjelleVarselRepository bjelleVarselRepository;

    static List<BjelleVarsel> lagVarslerGodkjenningerOpphevetArbeidsgiver (boolean erGodkjentAvDeltaker, BjelleVarselFactory factory) {
        var varslinger = new ArrayList<BjelleVarsel>();
        if (erGodkjentAvDeltaker) {
            varslinger.add(factory.deltaker(VarslbarStatus.VARSEL));
        } else varslinger.add(factory.deltaker(VarslbarStatus.LOGG));

        varslinger.add(factory.arbeidsgiver(VarslbarStatus.LOGG));
        varslinger.add(factory.veileder(VarslbarStatus.VARSEL));
        return  varslinger;
    }

    static List<BjelleVarsel> lagVarslerGodkjenningerOpphevetVeileder(GamleVerdier gamleVerdier, BjelleVarselFactory factory) {
        var varslinger = new ArrayList<BjelleVarsel>();
        if (gamleVerdier.isGodkjentAvDeltaker()) {
            varslinger.add(factory.deltaker(VarslbarStatus.VARSEL));
        } else varslinger.add(factory.deltaker(VarslbarStatus.LOGG));

        if (gamleVerdier.isGodkjentAvArbeidsgiver()) {
            varslinger.add(factory.arbeidsgiver(VarslbarStatus.VARSEL));
        } else varslinger.add(factory.arbeidsgiver(VarslbarStatus.LOGG));

        varslinger.add(factory.veileder(VarslbarStatus.LOGG));
        return varslinger;
    }


    static List<BjelleVarsel> lagBjelleVarsler(Avtale avtale, VarslbarHendelse varslbarHendelse, GamleVerdier gamleVerdier) {
        var factory = new BjelleVarselFactory(avtale, varslbarHendelse);
        VarslbarStatus VARSEL = VarslbarStatus.VARSEL;
        VarslbarStatus LOGG = VarslbarStatus.LOGG;

        switch (varslbarHendelse.getVarslbarHendelseType()) {
            case OPPRETTET:
            case GODKJENT_AV_VEILEDER:
                return List.of(factory.deltaker(VARSEL), factory.arbeidsgiver(VARSEL), factory.veileder(LOGG));
            case GODKJENT_AV_DELTAKER:
            case GODKJENT_AV_ARBEIDSGIVER:
                return List.of(factory.veileder(VARSEL), factory.arbeidsgiver(LOGG), factory.deltaker(LOGG));
            case TILSKUDDSPERIODE_AVSLATT:
                return List.of(factory.veileder(VARSEL));
            case GODKJENT_PAA_VEGNE_AV:
                return List.of(factory.arbeidsgiver(VARSEL), factory.deltaker(LOGG), factory.veileder(LOGG));
            case GODKJENNINGER_OPPHEVET_AV_ARBEIDSGIVER:
                return lagVarslerGodkjenningerOpphevetArbeidsgiver(gamleVerdier.isGodkjentAvDeltaker(), factory);
            case GODKJENNINGER_OPPHEVET_AV_VEILEDER:
                return lagVarslerGodkjenningerOpphevetVeileder(gamleVerdier, factory);
            case OPPRETTET_AV_ARBEIDSGIVER:
            case ENDRET:
            case LÅST_OPP:
            case AVBRUTT:
            case NY_VEILEDER:
            case AVTALE_FORDELT:
                return List.of(factory.arbeidsgiver(LOGG), factory.deltaker(LOGG), factory.veileder(LOGG));
        }
        return Collections.emptyList();
    }

    @EventListener
    public void lagreBjelleVarsler(VarslbarHendelseOppstaatt event) {
        bjelleVarselRepository.saveAll(lagBjelleVarsler(event.getAvtale(), event.getVarslbarHendelse(), event.getGamleVerdier()));
    }
}
