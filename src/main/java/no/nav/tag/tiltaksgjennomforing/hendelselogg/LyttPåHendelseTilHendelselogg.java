package no.nav.tag.tiltaksgjennomforing.hendelselogg;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.Avtalerolle;
import no.nav.tag.tiltaksgjennomforing.avtale.events.*;
import no.nav.tag.tiltaksgjennomforing.varsel.VarslbarHendelseType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LyttPåHendelseTilHendelselogg {
    private final HendelseloggRepository repository;

    @EventListener
    public void avtaleOpprettetAvVeileder(AvtaleOpprettetAvVeileder event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.VEILEDER, VarslbarHendelseType.OPPRETTET);
        repository.save(hendelselogg);
    }

    @EventListener
    public void avtaleOpprettetAvArbeidsgiver(AvtaleOpprettetAvArbeidsgiver event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.ARBEIDSGIVER, VarslbarHendelseType.OPPRETTET_AV_ARBEIDSGIVER);
        repository.save(hendelselogg);
    }

    @EventListener
    public void avtaleEndret(AvtaleEndret event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), event.getUtfortAv(), VarslbarHendelseType.ENDRET);
        repository.save(hendelselogg);
    }

    @EventListener
    public void godkjentAvDeltaker(GodkjentAvDeltaker event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.DELTAKER, VarslbarHendelseType.GODKJENT_AV_DELTAKER);
        repository.save(hendelselogg);
    }

    @EventListener
    public void godkjentAvArbeidsgiver(GodkjentAvArbeidsgiver event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.ARBEIDSGIVER, VarslbarHendelseType.GODKJENT_AV_ARBEIDSGIVER);
        repository.save(hendelselogg);
    }

    @EventListener
    public void godkjentAvVeileder(GodkjentAvVeileder event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.VEILEDER, VarslbarHendelseType.GODKJENT_AV_VEILEDER);
        repository.save(hendelselogg);
    }

    @EventListener
    public void godkjentPaVegneAv(GodkjentPaVegneAv event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.VEILEDER, VarslbarHendelseType.GODKJENT_PAA_VEGNE_AV);
        repository.save(hendelselogg);
    }

    @EventListener
    public void godkjenningerOpphevetAvArbeidsgiver(GodkjenningerOpphevetAvArbeidsgiver event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.ARBEIDSGIVER, VarslbarHendelseType.GODKJENNINGER_OPPHEVET_AV_ARBEIDSGIVER);
        repository.save(hendelselogg);
    }

    @EventListener
    public void godkjenningerOpphevetAvVeileder(GodkjenningerOpphevetAvVeileder event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.VEILEDER, VarslbarHendelseType.GODKJENNINGER_OPPHEVET_AV_VEILEDER);
        repository.save(hendelselogg);
    }

    @EventListener
    public void avtaleLåstOpp(AvtaleLåstOpp event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.VEILEDER, VarslbarHendelseType.LÅST_OPP);
        repository.save(hendelselogg);
    }

    @EventListener
    public void avbruttAvVeileder(AvbruttAvVeileder event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.VEILEDER, VarslbarHendelseType.AVBRUTT);
        repository.save(hendelselogg);
    }

    @EventListener
    public void gjenopprettet(AvtaleGjenopprettet event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.VEILEDER, VarslbarHendelseType.GJENOPPRETTET);
        repository.save(hendelselogg);
    }

    @EventListener
    public void avtaleEndretVeileder(AvtaleNyVeileder event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.VEILEDER, VarslbarHendelseType.NY_VEILEDER);
        repository.save(hendelselogg);
    }

    @EventListener
    public void ufordeltAvtaleTildeltVeileder(AvtaleOpprettetAvArbeidsgiverErFordelt event) { // ok
        Hendelselogg hendelselogg = Hendelselogg.nyHendelse(event.getAvtale().getId(), Avtalerolle.VEILEDER, VarslbarHendelseType.AVTALE_FORDELT);
        repository.save(hendelselogg);
    }
}
