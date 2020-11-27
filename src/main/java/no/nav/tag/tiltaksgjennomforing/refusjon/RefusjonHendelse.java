package no.nav.tag.tiltaksgjennomforing.refusjon;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.avtale.events.GodkjentAvVeileder;
import no.nav.tag.tiltaksgjennomforing.avtale.events.GodkjentPaVegneAv;
import no.nav.tag.tiltaksgjennomforing.refusjon.RefusjonProducer;
import no.nav.tag.tiltaksgjennomforing.refusjon.Refusjonsmelding;
import no.nav.tag.tiltaksgjennomforing.varsel.kafka.StatistikkformidlingProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@ConditionalOnProperty("tiltaksgjennomforing.kafka.enabled")
@Component
@RequiredArgsConstructor
public class RefusjonHendelse {

    private final RefusjonProducer refusjonProducer;

    @TransactionalEventListener
    public void godkjentAvVeileder(GodkjentAvVeileder event) {
        refusjonProducer.publiserRefusjonsmelding(Refusjonsmelding.fraAvtale(event.getAvtale()));
    }

    @TransactionalEventListener
    public void godkjentPaVegneAv(GodkjentPaVegneAv event) {
        refusjonProducer.publiserRefusjonsmelding(Refusjonsmelding.fraAvtale(event.getAvtale()));
    }
}