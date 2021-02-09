package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;
import no.nav.tag.tiltaksgjennomforing.avtale.events.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VarslbarHendelseLytter {
    private final VarslbarHendelseRepository varslbarHendelseRepository;

    @EventListener
    public void avtaleOpprettet(AvtaleOpprettetAvVeileder event) {
        varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.OPPRETTET, Avtalerolle.VEILEDER));
    }

    @EventListener
    public void avtaleOpprettetAvArbeidsgiver(AvtaleOpprettetAvArbeidsgiver event) {
        varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.OPPRETTET_AV_ARBEIDSGIVER, Avtalerolle.ARBEIDSGIVER));
    }

    @EventListener
    public void avtaleDeltMedAvtalepart(AvtaleDeltMedAvtalepart event) {
        if (event.getAvtalepart() == Avtalerolle.ARBEIDSGIVER) {
            varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.DELT_MED_ARBEIDSGIVER, Avtalerolle.VEILEDER));
        } else if (event.getAvtalepart() == Avtalerolle.DELTAKER) {
            varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.DELT_MED_DELTAKER, Avtalerolle.VEILEDER));
        }
    }

    @EventListener
    public void tilskuddsperiodeAvslått(TilskuddsperiodeAvslått event) {
         varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.TILSKUDDSPERIODE_AVSLATT, Avtalerolle.BESLUTTER));
    }

    @EventListener
    public void tilskuddsperiodeGodkjent(TilskuddsperiodeGodkjent event) {
        varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.TILSKUDDSPERIODE_GODKJENT, Avtalerolle.BESLUTTER));
    }

    @EventListener
    public void avtaleEndret(AvtaleEndret event) {
        varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.ENDRET, event.getUtfortAv()));
    }

    @EventListener
    public void godkjenningerOpphevetAvArbeidsgiver(GodkjenningerOpphevetAvArbeidsgiver event) {
        varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.GODKJENNINGER_OPPHEVET_AV_ARBEIDSGIVER, event.getGamleVerdier(), Avtalerolle.ARBEIDSGIVER));
    }

    @EventListener
    public void godkjenningerOpphevetAvVeileder(GodkjenningerOpphevetAvVeileder event) {
        varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.GODKJENNINGER_OPPHEVET_AV_VEILEDER, event.getGamleVerdier(), Avtalerolle.VEILEDER));
    }

    @EventListener
    public void godkjentAvDeltaker(GodkjentAvDeltaker event) {
        varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.GODKJENT_AV_DELTAKER, Avtalerolle.DELTAKER));
    }

    @EventListener
    public void godkjentAvArbeidsgiver(GodkjentAvArbeidsgiver event) {
        varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.GODKJENT_AV_ARBEIDSGIVER, Avtalerolle.ARBEIDSGIVER));
    }

    @EventListener
    public void godkjentAvVeileder(GodkjentAvVeileder event) {
        varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.GODKJENT_AV_VEILEDER, Avtalerolle.VEILEDER));
    }

    @EventListener
    public void godkjentPaVegneAv(GodkjentPaVegneAv event) {
        varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.GODKJENT_PAA_VEGNE_AV, Avtalerolle.VEILEDER));
    }

    @EventListener
    public void nyVeileder(AvtaleNyVeileder event) {
        varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.NY_VEILEDER, Avtalerolle.VEILEDER));
    }

    @EventListener
    public void fordelt(AvtaleOpprettetAvArbeidsgiverErFordelt event) {
        varslbarHendelseRepository.save(VarslbarHendelse.nyHendelse(event.getAvtale(), VarslbarHendelseType.AVTALE_FORDELT, Avtalerolle.VEILEDER));
    }
}
