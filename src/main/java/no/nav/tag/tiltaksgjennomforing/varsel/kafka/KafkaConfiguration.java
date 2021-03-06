package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

@ConditionalOnProperty("tiltaksgjennomforing.kafka.enabled")
@Configuration
@EnableKafka
public class KafkaConfiguration {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapAddress;
  @Value("${javax.net.ssl.trustStore}")
  private String sslTruststoreLocationEnvKey;
  @Value("${javax.net.ssl.trustStorePassword}")
  private String sslTruststorePasswordEnvKey;
  @Value("${tiltaksgjennomforing.serviceuser.username}")
  private String tiltaksgjennomforingServiceuserUsername;
  @Value("${tiltaksgjennomforing.serviceuser.password}")
  private String tiltaksgjennomforingServiceuserPassword;

  @Bean
  public KafkaTemplate<String, Statistikkformidlingsmelding> kafkaTemplateStatistikkformidlingsmelding() {
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(getProducerConfigs()));
  }

  @Bean
  public KafkaTemplate<String, SmsVarselMelding> kafkaTemplateSmsVarselMelding() {
    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(getProducerConfigs()));
  }

  @NotNull
  private Map<String, Object> getProducerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
    props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslTruststoreLocationEnvKey);
    props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslTruststorePasswordEnvKey);
    props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_SSL.name);
    props.put(SaslConfigs.SASL_JAAS_CONFIG,
        "org.apache.kafka.common.security.plain.PlainLoginModule required username='" + tiltaksgjennomforingServiceuserUsername + "' password='"
            + tiltaksgjennomforingServiceuserPassword + "';");

    return props;
  }
}
