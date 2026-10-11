package gigabank.service.kafka;

import gigabank.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterPublisher {

    private final KafkaTemplate<String, TransactionDto> atLeastOnceKafkaTemplate;

    public void publish(TransactionDto transactionDto, Exception error, int attempt) {
        String topic = "dlt";
        ProducerRecord<String, TransactionDto> record =
                new ProducerRecord<>(topic, transactionDto.getAccountId(), transactionDto);
        record.headers().add("x-error-class",
                error.getClass().getName().getBytes(StandardCharsets.UTF_8));
        record.headers().add("x-error-message",
                String.valueOf(error.getMessage()).getBytes(StandardCharsets.UTF_8));
        record.headers().add("x-attempt",
                String.valueOf(attempt).getBytes(StandardCharsets.UTF_8));

        atLeastOnceKafkaTemplate.send(record);
        log.error("Sent to DLT [{}]: transactionId={}, account={}, attempt={}, error={}",
                topic, transactionDto.getTransactionId(), transactionDto.getAccountId(), attempt, error.toString());
    }
}
