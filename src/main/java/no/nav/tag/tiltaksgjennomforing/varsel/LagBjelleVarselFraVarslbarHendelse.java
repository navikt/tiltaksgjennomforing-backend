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
            varslinger.add(factory.deltaker(VarslbarStatusNiva.HOY));
        } else varslinger.add(factory.deltaker(VarslbarStatusNiva.LAV));

        varslinger.add(factory.arbeidsgiver(VarslbarStatusNiva.LAV));
        varslinger.add(factory.veileder(VarslbarStatusNiva.HOY));
        return  varslinger;
    }

    static List<BjelleVarsel> lagVarslerGodkjenningerOpphevetVeileder(GamleVerdier gamleVerdier, BjelleVarselFactory factory) {
        var varslinger = new ArrayList<BjelleVarsel>();
        if (gamleVerdier.isGodkjentAvDeltaker()) {
            varslinger.add(factory.deltaker(VarslbarStatusNiva.HOY));
        } else varslinger.add(factory.deltaker(VarslbarStatusNiva.LAV));

        if (gamleVerdier.isGodkjentAvArbeidsgiver()) {
            varslinger.add(factory.arbeidsgiver(VarslbarStatusNiva.HOY));
        } else varslinger.add(factory.arbeidsgiver(VarslbarStatusNiva.LAV));

        varslinger.add(factory.veileder(VarslbarStatusNiva.LAV));
        return varslinger;
    }


    static List<BjelleVarsel> lagBjelleVarsler(Avtale avtale, VarslbarHendelse varslbarHendelse, GamleVerdier gamleVerdier) {
        var factory = new BjelleVarselFactory(avtale, varslbarHendelse);
        VarslbarStatusNiva HOY = VarslbarStatusNiva.HOY;
        VarslbarStatusNiva LAV = VarslbarStatusNiva.LAV;

        switch (varslbarHendelse.getVarslbarHendelseType()) {
            case OPPRETTET:
            case GODKJENT_AV_VEILEDER:
                return List.of(factory.deltaker(HOY), factory.arbeidsgiver(HOY), factory.veileder(LAV));
            case GODKJENT_AV_DELTAKER:
            case GODKJENT_AV_ARBEIDSGIVER:
                return List.of(factory.veileder(HOY), factory.arbeidsgiver(LAV), factory.deltaker(LAV));
            case TILSKUDDSPERIODE_AVSLATT:
                return List.of(factory.veileder(HOY));
            case GODKJENT_PAA_VEGNE_AV:
                return List.of(factory.arbeidsgiver(HOY), factory.deltaker(LAV), factory.veileder(LAV));
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
                return List.of(factory.arbeidsgiver(LAV), factory.deltaker(LAV), factory.veileder(LAV));
        }
        return Collections.emptyList();
    }

    @EventListener
    public void lagreBjelleVarsler(VarslbarHendelseOppstaatt event) {
        bjelleVarselRepository.saveAll(lagBjelleVarsler(event.getAvtale(), event.getVarslbarHendelse(), event.getGamleVerdier()));
    }
}
