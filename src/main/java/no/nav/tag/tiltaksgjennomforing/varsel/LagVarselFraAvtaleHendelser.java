package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;
import no.nav.tag.tiltaksgjennomforing.avtale.events.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LagVarselFraAvtaleHendelser {
    private final VarselRepository varselRepository;

    @EventListener
    public void avtaleOpprettet(AvtaleOpprettetAvVeileder event) {
        VarselFactory factory = new VarselFactory(event.getAvtale(), Avtalerolle.VEILEDER, VarslbarHendelseType.OPPRETTET);
        varselRepository.saveAll(factory.veileder(false), factory.arbeidsgiver(true), factory.deltaker(true));
    }

    @EventListener
    public void avtaleOpprettetAvArbeidsgiver(AvtaleOpprettetAvArbeidsgiver event) {
        VarselFactory factory = new VarselFactory(event.getAvtale(), Avtalerolle.VEILEDER, VarslbarHendelseType.OPPRETTET_AV_ARBEIDSGIVER);
        varselRepository.saveAll(factory.veileder(true), factory.arbeidsgiver(false), factory.deltaker(true));
    }

    @EventListener
    public void avtaleDeltMedAvtalepart(AvtaleDeltMedAvtalepart event) {
        if (event.getAvtalepart() == Avtalerolle.ARBEIDSGIVER) {
            VarselFactory factory = new VarselFactory(event.getAvtale(), Avtalerolle.VEILEDER, VarslbarHendelseType.DELT_MED_ARBEIDSGIVER);
            varselRepository.saveAll(factory.veileder(false), factory.arbeidsgiver(true));
        } else if (event.getAvtalepart() == Avtalerolle.DELTAKER) {
            VarselFactory factory = new VarselFactory(event.getAvtale(), Avtalerolle.VEILEDER, VarslbarHendelseType.DELT_MED_DELTAKER);
            varselRepository.saveAll(factory.veileder(false), factory.deltaker(true));
        }
    }

    @EventListener
    public void tilskuddsperiodeAvslått(TilskuddsperiodeAvslått event) {
        VarselFactory factory = new VarselFactory(event.getAvtale(), Avtalerolle.VEILEDER, VarslbarHendelseType.TILSKUDDSPERIODE_AVSLATT);
        varselRepository.saveAll(factory.veileder(false), factory.arbeidsgiver(true));
    }

    @EventListener
    public void tilskuddsperiodeGodkjent(TilskuddsperiodeGodkjent event) {
        VarselFactory factory = new VarselFactory(event.getAvtale(), Avtalerolle.VEILEDER, VarslbarHendelseType.TILSKUDDSPERIODE_GODKJENT);
        varselRepository.saveAll(factory.veileder(false), factory.arbeidsgiver(true));
    }

    @EventListener
    public void avtaleEndret(AvtaleEndret event) {
        VarselFactory factory = new VarselFactory(event.getAvtale(), Avtalerolle.VEILEDER, VarslbarHendelseType.ENDRET);
        varselRepository.saveAll(factory.veileder(event.getUtfortAv() == Avtalerolle.ARBEIDSGIVER), factory.arbeidsgiver(event.getUtfortAv() == Avtalerolle.VEILEDER));
    }

    @EventListener
    public void godkjenningerOpphevetAvArbeidsgiver(GodkjenningerOpphevetAvArbeidsgiver event) {
        VarselFactory factory = new VarselFactory(event.getAvtale(), Avtalerolle.VEILEDER, VarslbarHendelseType.GODKJENNINGER_OPPHEVET_AV_ARBEIDSGIVER);
        boolean varGodkjentAvDeltaker = event.getGamleVerdier().isGodkjentAvDeltaker();
        varselRepository.saveAll(factory.arbeidsgiver(false), factory.deltaker(varGodkjentAvDeltaker), factory.veileder(true));
    }

    @EventListener
    public void godkjenningerOpphevetAvVeileder(GodkjenningerOpphevetAvVeileder event) {
        VarselFactory factory = new VarselFactory(event.getAvtale(), Avtalerolle.VEILEDER, VarslbarHendelseType.GODKJENT_AV_VEILEDER);
        boolean varGodkjentAvDeltaker = event.getGamleVerdier().isGodkjentAvDeltaker();
        boolean varGodkjentAvArbeidsgiver = event.getGamleVerdier().isGodkjentAvDeltaker();
        varselRepository.saveAll(factory.arbeidsgiver(varGodkjentAvArbeidsgiver), factory.deltaker(varGodkjentAvDeltaker), factory.veileder(true));
    }

    @EventListener
    public void godkjentAvDeltaker(GodkjentAvDeltaker event) {

    }

    @EventListener
    public void godkjentAvArbeidsgiver(GodkjentAvArbeidsgiver event) {

    }

    @EventListener
    public void godkjentAvVeileder(GodkjentAvVeileder event) {

    }

    @EventListener
    public void godkjentPaVegneAv(GodkjentPaVegneAv event) {

    }

    @EventListener
    public void nyVeileder(AvtaleNyVeileder event) {

    }

    @EventListener
    public void fordelt(AvtaleOpprettetAvArbeidsgiverErFordelt event) {

    }

    @EventListener
    public void avbrutt(AvbruttAvVeileder event) {

    }

    @EventListener
    public void låstOpp(AvtaleLåstOpp event) {

    }

    @EventListener
    public void gjenopprettet(AvtaleGjenopprettet event) {

    }
}
