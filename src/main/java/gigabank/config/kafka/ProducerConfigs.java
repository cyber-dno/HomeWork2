package gigabank.config.kafka;

import gigabank.dto.TransactionDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProducerConfigs {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, TransactionDto> atMostOnceProducerFactory() {
        Map<String, Object> props = baseProps();
        props.put(ProducerConfig.ACKS_CONFIG, "0");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, TransactionDto> atMostOnceKafkaTemplate() {
        return new KafkaTemplate<>(atMostOnceProducerFactory());
    }

    @Bean
    public ProducerFactory<String, TransactionDto> atLeastOnceProducerFactory() {
        Map<String, Object> props = baseProps();
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 10);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, TransactionDto> atLeastOnceKafkaTemplate() {
        return new KafkaTemplate<>(atLeastOnceProducerFactory());
    }

    @Bean
    public ProducerFactory<String, TransactionDto> exactlyOnceProducerFactory() {
        Map<String, Object> props = baseProps();
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 10);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        DefaultKafkaProducerFactory<String, TransactionDto> factory =
                new DefaultKafkaProducerFactory<>(props);
        factory.setTransactionIdPrefix("producer-");
        return factory;
    }

    @Bean
    public KafkaTemplate<String, TransactionDto> exactlyOnceKafkaTemplate() {
        return new KafkaTemplate<>(exactlyOnceProducerFactory());
    }

    @Bean
    public KafkaTransactionManager<String, TransactionDto>
    kafkaTransactionManager() {
        return new KafkaTransactionManager<>(
                exactlyOnceProducerFactory());
    }
}
