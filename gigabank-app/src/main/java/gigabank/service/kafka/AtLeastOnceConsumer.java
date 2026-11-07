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
public class AtLeastOnceConsumer {
    private final TransactionProcessingService transactionProcessingService;
    private final MetricsService metricsService;
    private final RetryHandler retryHandler;

    @KafkaListener(
            topics = "transactions",
            groupId = "at-least-once",
            containerFactory = "atLeastOnceFactory"
    )
    public void consume(TransactionDto transactionDto, Acknowledgment acknowledgment) {
        long start = System.currentTimeMillis();
        ProcessingMetrics m = metricsService.forMode("at-least-once");
        try {
            boolean ok = retryHandler.executeWithRetry(
                    transactionDto,
                    transactionProcessingService::process,
                    m::incRetry);
            if (ok) {
                m.incProcessed(1);
                log.info("Processed (at-least-once) transactionId={}", transactionDto.getTransactionId());
            } else {
                m.incError();
            }
        } catch (Exception e) {
            m.incError();
            log.error("Unhandled error (at-least-once) transactionId={}", transactionDto.getTransactionId(), e);
        } finally {
            m.addSingleTime(System.currentTimeMillis() - start);
            // Ручной коммит оффсета после обработки
            acknowledgment.acknowledge();
        }
    }
}
