package gigabank.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name("transactions")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic retryTopic() {
        return TopicBuilder.name("retry")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic dltTopic() {
        return TopicBuilder.name("dlt")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
