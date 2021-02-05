package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtale;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;
import no.nav.tag.tiltaksgjennomforing.avtale.events.AvtaleOpprettetAvArbeidsgiver;
import no.nav.tag.tiltaksgjennomforing.avtale.events.GamleVerdier;
import no.nav.tag.tiltaksgjennomforing.varsel.events.VarslbarHendelseOppstaatt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static no.nav.tag.tiltaksgjennomforing.varsel.VarslbarStatus.LOGG;
import static no.nav.tag.tiltaksgjennomforing.varsel.VarslbarStatus.VARSEL;

@Component
@RequiredArgsConstructor
public class LagVarselFraVarslbarHendelse {
    private final VarselRepository varselRepository;

    static List<Varsel> lagVarslerGodkjenningerOpphevetArbeidsgiver (boolean erGodkjentAvDeltaker, VarselFactory factory) {
        var varslinger = new ArrayList<Varsel>();
        if (erGodkjentAvDeltaker) {
            varslinger.add(factory.deltaker(VARSEL));
        } else varslinger.add(factory.deltaker(LOGG));

        varslinger.add(factory.arbeidsgiver(LOGG));
        varslinger.add(factory.veileder(VARSEL));
        return  varslinger;
    }

    static List<Varsel> lagVarslerGodkjenningerOpphevetVeileder(GamleVerdier gamleVerdier, VarselFactory factory) {
        var varslinger = new ArrayList<Varsel>();
        if (gamleVerdier.isGodkjentAvDeltaker()) {
            varslinger.add(factory.deltaker(VARSEL));
        } else varslinger.add(factory.deltaker(LOGG));

        if (gamleVerdier.isGodkjentAvArbeidsgiver()) {
            varslinger.add(factory.arbeidsgiver(VARSEL));
        } else varslinger.add(factory.arbeidsgiver(LOGG));

        varslinger.add(factory.veileder(LOGG));
        return varslinger;
    }


    static List<Varsel> lagBjelleVarsler(Avtale avtale, VarslbarHendelse varslbarHendelse, GamleVerdier gamleVerdier, Avtalerolle utførtAv) {
        var factory = new VarselFactory(avtale, varslbarHendelse, utførtAv);

        switch (varslbarHendelse.getVarslbarHendelseType()) {
            case OPPRETTET:
            case GODKJENT_AV_VEILEDER:
                return List.of(factory.deltaker(VARSEL), factory.arbeidsgiver(VARSEL), factory.veileder(LOGG));
            case GODKJENT_AV_DELTAKER:
            case GODKJENT_AV_ARBEIDSGIVER:
                return List.of(factory.veileder(VARSEL), factory.arbeidsgiver(LOGG), factory.deltaker(LOGG));
            case TILSKUDDSPERIODE_AVSLATT:
            case TILSKUDDSPERIODE_GODKJENT:
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
    public void lagreVarsler(VarslbarHendelseOppstaatt event) {
        varselRepository.saveAll(lagBjelleVarsler(event.getAvtale(), event.getVarslbarHendelse(), event.getGamleVerdier(), event.getUtførtAv()));
    }
}
