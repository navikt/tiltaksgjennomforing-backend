package no.nav.tag.tiltaksgjennomforing.integrasjon.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.domene.exceptions.TiltaksgjennomforingException;
import no.nav.tag.tiltaksgjennomforing.domene.varsel.VarselService;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Profile("kafka")
@Component
@RequiredArgsConstructor
@Slf4j
public class SmsVarselConsumer {
    private final VarselService varselService;
    private final SmsVarselResultatProducer resultatProducer;

    @KafkaListener(groupId = "smsVarselConsumer", topics = Topics.SMS_VARSEL)
    public void consume(SmsVarselMelding varselMelding) {
        SmsVarselResultatMelding resultatMelding;
        try {
            varselService.sendVarsel(varselMelding.getAvgiver(), varselMelding.getTelefonnummer(), varselMelding.getVarseltekst());
            resultatMelding = SmsVarselResultatMelding.sendt(varselMelding);
        } catch (TiltaksgjennomforingException e) {
            resultatMelding = SmsVarselResultatMelding.feil(varselMelding);
        }
        resultatProducer.sendSmsVarselResultatMeldingTilKafka(resultatMelding);
    }
}