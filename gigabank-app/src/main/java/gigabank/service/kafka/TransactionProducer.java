package gigabank.service.kafka;

import gigabank.dto.TransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TransactionProducer {
    private static final int ACCOUNT_COUNT = 10;
    private static final String TOPIC = "transactions";

    private final KafkaTemplate<String, TransactionDto> atMostOnce;
    private final KafkaTemplate<String, TransactionDto> atLeastOnce;
    private final KafkaTemplate<String, TransactionDto> exactlyOnce;

    public TransactionProducer(@Qualifier("atMostOnceKafkaTemplate") KafkaTemplate<String, TransactionDto> atMostOnce,
                               @Qualifier("atLeastOnceKafkaTemplate") KafkaTemplate<String, TransactionDto> atLeastOnce,
                               @Qualifier("exactlyOnceKafkaTemplate") KafkaTemplate<String, TransactionDto> exactlyOnce) {
        this.atMostOnce = atMostOnce;
        this.atLeastOnce = atLeastOnce;
        this.exactlyOnce = exactlyOnce;
    }

    public void sendAtMostOnce(int count) {
        for (int i = 0; i < count; i++) {
            String accountId = "ACCOUNT_" + (int) (Math.random() * ACCOUNT_COUNT);
            TransactionDto transactionDto = TransactionDto.randomTransaction(accountId);
            atMostOnce.send(new ProducerRecord<>(TOPIC, transactionDto.getAccountId(), transactionDto));
        }
        log.info("Sent {} messages (at-most-once)", count);
    }

    public void sendAtLeastOnce(int count) {
        for (int i = 0; i < count; i++) {
            String accountId = "ACCOUNT_" + (int) (Math.random() * ACCOUNT_COUNT);
            TransactionDto transactionDto = TransactionDto.randomTransaction(accountId);
            atLeastOnce.send(new ProducerRecord<>(TOPIC, transactionDto.getAccountId(), transactionDto));
        }
        log.info("Sent {} messages (at-least-once)", count);
    }

    @Transactional("kafkaTransactionManager")
    public void sendExactlyOnce(int count) {
        for (int i = 0; i < count; i++) {
            String accountId = "ACCOUNT_" + (int) (Math.random() * ACCOUNT_COUNT);
            TransactionDto transactionDto = TransactionDto.randomTransaction(accountId);
            exactlyOnce.send(new ProducerRecord<>(TOPIC, transactionDto.getAccountId(), transactionDto));
        }
        log.info("Sent {} messages (exactly-once, transactional)", count);
    }
}
