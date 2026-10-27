package gigabank.service.kafka;

import gigabank.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchConsumer {
    private final TransactionProcessingService transactionProcessingService;
    private final MetricsService metricsService;
    private final DeadLetterPublisher deadLetterPublisher;

    @KafkaListener(
            topics = "transactions",
            groupId = "batch",
            containerFactory = "batchFactory"
    )
    public void consume(List<TransactionDto> batch, Acknowledgment acknowledgment) {
        long start = System.currentTimeMillis();
        ProcessingMetrics m = metricsService.forMode("batch");
        try {
            // Атомарная обработка батча: одно падение — весь батч откатывается.
            for (TransactionDto tx : batch) {
                transactionProcessingService.processIdempotent(tx);
            }
            m.incProcessed(batch.size());
            m.incBatch();
            log.info("Processed batch of {} messages in {} ms",
                    batch.size(), System.currentTimeMillis() - start);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            m.incError();
            log.error("Batch failed, sending all {} messages to DLT. Reason: {}",
                    batch.size(), e.getMessage(), e);
            for (TransactionDto tx : batch) {
                deadLetterPublisher.publish(tx, e, 1);
            }
            // Коммитим, чтобы не зациклиться на батче
            acknowledgment.acknowledge();
        } finally {
            m.addBatchTime(System.currentTimeMillis() - start);
        }
    }
}
