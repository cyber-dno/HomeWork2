package gigabank.service.kafka;

import gigabank.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExactlyOnceConsumer {
    private final TransactionProcessingService transactionProcessingService;
    private final MetricsService metricsService;

    @KafkaListener(
            topics = "transactions",
            groupId = "exactly-once",
            containerFactory = "exactlyOnceFactory"
    )
    public void consume(TransactionDto transactionDto, Acknowledgment acknowledgment) {
        long start = System.currentTimeMillis();
        ProcessingMetrics m = metricsService.forMode("exactly-once");
        try {
            boolean applied = transactionProcessingService.processIdempotent(transactionDto);
            if (applied) {
                m.incProcessed(1);
                log.info("Applied (exactly-once) transactionId={}", transactionDto.getTransactionId());
            } else {
                m.incDuplicate();
                log.info("Skipped duplicate (exactly-once) transactionId={}", transactionDto.getTransactionId());
            }
        } catch (Exception e) {
            m.incError();
            log.error("Failed (exactly-once) transactionId={}: {}", transactionDto.getTransactionId(), e.getMessage());
        } finally {
            m.addSingleTime(System.currentTimeMillis() - start);
            acknowledgment.acknowledge();
        }
    }
}
