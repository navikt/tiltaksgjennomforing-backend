package no.nav.tag.tiltaksgjennomforing.integrasjon.kafka;

import lombok.RequiredArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.domene.events.VarslbarHendelseOppstaatt;
import no.nav.tag.tiltaksgjennomforing.domene.varsel.SmsVarsel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Profile("kafka")
@Component
@RequiredArgsConstructor
public class SmsVarselLytter {
    private final SmsVarselProducer producer;
    private final SmsVarselMeldingMapper mapper;

    @TransactionalEventListener
    public void opprettSmsVarsler(VarslbarHendelseOppstaatt event) {
        for (SmsVarsel smsVarsel : event.getVarslbarHendelse().getSmsVarsler()) {
            producer.sendSmsVarselMeldingTilKafka(mapper.tilMelding(smsVarsel));
        }
    }
}
