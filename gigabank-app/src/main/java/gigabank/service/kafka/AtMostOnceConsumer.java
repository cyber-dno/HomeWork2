package gigabank.service.kafka;

import gigabank.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AtMostOnceConsumer {
    private final TransactionProcessingService transactionProcessingService;
    private final MetricsService metricsService;

    @KafkaListener(
            topics = "transactions",
            groupId = "at-most-once",
            containerFactory = "atMostOnceFactory"
    )
    public void consume(TransactionDto transactionDto) {
        long start = System.currentTimeMillis();
        try {
            transactionProcessingService.process(transactionDto);
            metricsService.forMode("at-most-once").incProcessed(1);
            log.info("Processed (at-most-once) transactionId={} account={}",
                    transactionDto.getTransactionId(), transactionDto.getAccountId());
        } catch (Exception e) {
            metricsService.forMode("at-most-once").incError();
            log.error("Failed (at-most-once) transactionId={}: {}", transactionDto.getTransactionId(), e.getMessage());
        } finally {
            metricsService.forMode("at-most-once")
                    .addSingleTime(System.currentTimeMillis() - start);
        }
    }
}
