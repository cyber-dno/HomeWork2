package gigabank.service;

import gigabank.model.OutboxMessage;
import gigabank.repository.OutboxMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class OutboxProcessor {
    private final OutboxMessageRepository outboxMessageRepository;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void processOutboxMessages() {
        List<OutboxMessage> messages = outboxMessageRepository
                .findByProcessedFalseOrderByCreatedAtAsc();

        for (OutboxMessage message : messages) {
            try {
                // 1. Отправка в Kafka
                // kafkaTemplate.send("transactions", message.getPayload());

                // 2. Отметка как обработанного
                message.setProcessed(true);
                outboxMessageRepository.save(message);

            } catch (Exception e) {
                log.error("Ошибка обработки сообщения {}: {}", message.getId(), e.getMessage());
            }
        }
    }
}
