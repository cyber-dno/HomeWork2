package gigabank.service.kafka;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Data
public class ProcessingMetrics {
    private final LongAdder processed = new LongAdder();
    private final LongAdder errors = new LongAdder();
    private final LongAdder retries = new LongAdder();
    private final LongAdder duplicates = new LongAdder();
    private final LongAdder batches = new LongAdder();

    private final AtomicLong totalBatchTimeMs = new AtomicLong();
    private final AtomicLong totalSingleTimeMs = new AtomicLong();

    public void incProcessed(long n) { processed.add(n); }
    public void incError() { errors.increment(); }
    public void incRetry() { retries.increment(); }
    public void incDuplicate() { duplicates.increment(); }
    public void incBatch() { batches.increment(); }
    public void addBatchTime(long ms) { totalBatchTimeMs.addAndGet(ms); }
    public void addSingleTime(long ms) { totalSingleTimeMs.addAndGet(ms); }

    public double avgBatchTimeMs() {
        long b = batches.sum();
        return b == 0 ? 0 : (double) totalBatchTimeMs.get() / b;
    }

    public double avgSingleTimeMs() {
        long p = processed.sum();
        return p == 0 ? 0 : (double) totalSingleTimeMs.get() / p;
    }
}
