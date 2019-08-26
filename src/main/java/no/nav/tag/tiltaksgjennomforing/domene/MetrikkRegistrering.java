package no.nav.tag.tiltaksgjennomforing.domene;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.domene.events.*;
import no.nav.tag.tiltaksgjennomforing.integrasjon.configurationProperties.PilotProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MetrikkRegistrering {
    private final MeterRegistry meterRegistry;
    private final PilotProperties pilotProperties;

    private boolean pilotFylke(Identifikator utfortAv) {
        return !pilotProperties.getIdenter().contains(utfortAv);
    }

    @EventListener
    public void avtaleOpprettet(AvtaleOpprettet event) {
        log.info("Avtale opprettet, avtaleId={} ident={}, PilotFylke={}", event.getAvtale().getId(), event.getUtfortAv(), pilotFylke(event.getUtfortAv()));
        counter("avtale.opprettet", Avtalerolle.VEILEDER, pilotFylke(event.getUtfortAv())).increment();
    }

    @EventListener
    public void avtaleEndret(AvtaleEndret event) {
        log.info("Avtale endret, avtaleId={} avtalepart={}", event.getAvtale().getId(), event.getUtfortAv());
        counter("avtale.endret", event.getUtfortAv(), null).increment();
    }

    @EventListener
    public void godkjenningerOpphevet(GodkjenningerOpphevet event) {
        log.info("Avtalens godkjenninger opphevet, avtaleId={} avtalepart={}", event.getAvtale().getId(), event.getUtfortAv());
        counter("avtale.godkjenning.opphevet", event.getUtfortAv(), null).increment();
    }

    @EventListener
    public void godkjentAvDeltaker(GodkjentAvDeltaker event) {
        log.info("Avtale godkjent, avtaleId={} avtalepart=DELTAKER", event.getAvtale().getId());
        counter("avtale.godkjenning.godkjent", Avtalerolle.DELTAKER, null).increment();
    }

    @EventListener
    public void godkjentAvArbeidsgiver(GodkjentAvArbeidsgiver event) {
        log.info("Avtale godkjent, avtaleId={} avtalepart=ARBEIDSGIVER", event.getAvtale().getId());
        counter("avtale.godkjenning.godkjent", Avtalerolle.ARBEIDSGIVER, null).increment();
    }

    @EventListener
    public void godkjentAvVeileder(GodkjentAvVeileder event) {
        log.info("Avtale godkjent, avtaleId={} avtalepart=VEILEDER, PilotFylke={}", event.getAvtale().getId(), pilotFylke(event.getUtfortAv()));
        counter("avtale.godkjenning.godkjent", Avtalerolle.VEILEDER, pilotFylke(event.getUtfortAv())).increment();
    }

    @EventListener
    public void godkjentPaVegneAv(GodkjentPaVegneAv event) {
        log.info("Avtale godkjent på vegne av deltaker, avtaleId={} avtalepart=VEILEDER, PilotFylke={}", event.getAvtale().getId(), pilotFylke(event.getUtfortAv()));
        counter("avtale.godkjenning.godkjentPaVegneAv", Avtalerolle.VEILEDER, pilotFylke(event.getUtfortAv())).increment();
    }

    private Counter counter(String navn, Avtalerolle avtalerolle, Boolean erPilotFylke) {
        var builder = Counter.builder("tiltaksgjennomforing." + navn)
                .tag("tiltak", Tiltaktype.ARBEIDSTRENING.name())
                .tag("avtalepart", avtalerolle.name());
        if (erPilotFylke != null) {
            builder.tag("pilotfylke", erPilotFylke.toString());
        }
        return builder.register(meterRegistry);
    }

}
