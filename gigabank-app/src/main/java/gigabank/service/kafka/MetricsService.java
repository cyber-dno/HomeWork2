package gigabank.service.kafka;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MetricsService {
    private final Map<String, ProcessingMetrics> metricsByMode = new ConcurrentHashMap<>();

    public ProcessingMetrics forMode(String mode) {
        return metricsByMode.computeIfAbsent(mode, m -> new ProcessingMetrics());
    }

    public Map<String, ProcessingMetrics> snapshot() {
        return metricsByMode;
    }
}
