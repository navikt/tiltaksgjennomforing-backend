package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@ConditionalOnProperty("tiltaksgjennomforing.kafka.enabled")
@Component
@Slf4j
@RequiredArgsConstructor
public class SmsVarselProducer {
    private final KafkaTemplate<String, SmsVarselMelding> kafkaTemplate;

    public void sendSmsVarselMeldingTilKafka(SmsVarselMelding smsVarsel) {
        kafkaTemplate.send(Topics.SMS_VARSEL, smsVarsel.getSmsVarselId().toString(), smsVarsel).addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.warn("SmsVarsel med smsVarselId={} kunne ikke sendes til Kafka topic", smsVarsel.getSmsVarselId());
            }

            @Override
            public void onSuccess(SendResult<String, SmsVarselMelding> result) {
                log.info("SmsVarsel med smsVarselId={} sendt på Kafka topic", smsVarsel.getSmsVarselId());
            }
        });
    }
}
