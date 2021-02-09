package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;
import no.nav.tag.tiltaksgjennomforing.avtale.events.GamleVerdier;
import no.nav.tag.tiltaksgjennomforing.varsel.events.VarslbarHendelseOppstaatt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LagVarselFraVarslbarHendelse {
    private final VarselRepository varselRepository;

    static List<Varsel> lagVarslerGodkjenningerOpphevetArbeidsgiver(boolean erGodkjentAvDeltaker, VarselFactory factory) {
        var varslinger = new ArrayList<Varsel>();
        if (erGodkjentAvDeltaker) {
            varslinger.add(factory.deltaker(true));
        } else varslinger.add(factory.deltaker(false));

        varslinger.add(factory.arbeidsgiver(false));
        varslinger.add(factory.veileder(true));
        return varslinger;
    }

    static List<Varsel> lagVarslerGodkjenningerOpphevetVeileder(GamleVerdier gamleVerdier, VarselFactory factory) {
        var varslinger = new ArrayList<Varsel>();
        if (gamleVerdier.isGodkjentAvDeltaker()) {
            varslinger.add(factory.deltaker(true));
        } else varslinger.add(factory.deltaker(false));

        if (gamleVerdier.isGodkjentAvArbeidsgiver()) {
            varslinger.add(factory.arbeidsgiver(true));
        } else varslinger.add(factory.arbeidsgiver(false));

        varslinger.add(factory.veileder(false));
        return varslinger;
    }


    static List<Varsel> lagBjelleVarsler(Avtale avtale, VarslbarHendelse varslbarHendelse, GamleVerdier gamleVerdier, Avtalerolle utførtAv) {
        var factory = new VarselFactory(avtale, utførtAv, varslbarHendelse.getVarslbarHendelseType());

        switch (varslbarHendelse.getVarslbarHendelseType()) {
            case OPPRETTET:
            case GODKJENT_AV_VEILEDER:
            case GJENOPPRETTET:
                return List.of(factory.deltaker(true), factory.arbeidsgiver(true), factory.veileder(false));
            case GODKJENT_AV_DELTAKER:
            case GODKJENT_AV_ARBEIDSGIVER:
                return List.of(factory.veileder(true), factory.arbeidsgiver(false), factory.deltaker(false));
            case TILSKUDDSPERIODE_AVSLATT:
            case TILSKUDDSPERIODE_GODKJENT:
                return List.of(factory.veileder(true));
            case GODKJENT_PAA_VEGNE_AV:
                return List.of(factory.arbeidsgiver(true), factory.deltaker(false), factory.veileder(false));
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
                return List.of(factory.arbeidsgiver(false), factory.deltaker(false), factory.veileder(false));
            case DELT_MED_DELTAKER:
            case DELT_MED_ARBEIDSGIVER:
                return List.of(factory.veileder(false));

        }
        return Collections.emptyList();
    }

    @EventListener
    public void lagreVarsler(VarslbarHendelseOppstaatt event) {
        varselRepository.saveAll(lagBjelleVarsler(event.getAvtale(), event.getVarslbarHendelse(), event.getGamleVerdier(), event.getUtførtAv()));
    }
}
