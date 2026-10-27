package gigabank.service.kafka;

import gigabank.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryHandler {

    private final DeadLetterPublisher deadLetterPublisher;

    public boolean executeWithRetry(TransactionDto tx,
                                    Consumer<TransactionDto> action,
                                    Runnable onRetry) {
        int attempt = 0;
        int maxAttempts = 3;
        while (attempt < maxAttempts) {
            attempt++;
            try {
                action.accept(tx);
                return true;
            } catch (Exception ex) {
                log.warn("Attempt {}/{} failed for transactionId={}: {}",
                        attempt, maxAttempts, tx.getTransactionId(), ex.getMessage());
                if (attempt < maxAttempts) {
                    if (onRetry != null) onRetry.run();
                    long backoffMs = 500;
                    sleep(backoffMs * attempt);
                } else {
                    deadLetterPublisher.publish(tx, ex, attempt);
                    return false;
                }
            }
        }
        return false;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
