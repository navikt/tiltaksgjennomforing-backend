package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import no.nav.tag.tiltaksgjennomforing.Miljø;
import no.nav.tag.tiltaksgjennomforing.avtale.Identifikator;
import no.nav.tag.tiltaksgjennomforing.varsel.SmsVarsel;
import no.nav.tag.tiltaksgjennomforing.varsel.SmsVarselRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest(properties = {"tiltaksgjennomforing.kafka.enabled=true"})
@ActiveProfiles({Miljø.LOCAL})
@EmbeddedKafka(partitions = 1, controlledShutdown = false, topics = {Topics.SMS_VARSEL, Topics.SMS_VARSEL_RESULTAT})
public class SmsVarselProducerTest {

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;
    @Autowired
    private SmsVarselRepository repository;

    private Consumer<String, String> consumer;

    @Before
    public void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "false", embeddedKafka);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        ConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = cf.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, Topics.SMS_VARSEL);
    }

    @Test
    public void smsVarselOpprettet__skal_sendes_på_kafka_topic_med_riktige_felter() throws JSONException {
        transactionTemplate.execute(status -> repository.save(SmsVarsel.nyttVarsel("tlf", new Identifikator("id"), "melding", null)));

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, Topics.SMS_VARSEL);
        JSONObject json = new JSONObject(record.value());
        assertThat(json.getString("smsVarselId")).isNotNull();
        assertThat(json.getString("identifikator")).isEqualTo("id");
        assertThat(json.getString("meldingstekst")).isEqualTo("melding");
        assertThat(json.getString("telefonnummer")).isEqualTo("tlf");
    }
}
