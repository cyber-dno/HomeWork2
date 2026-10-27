package gigabank.controller;

import gigabank.dto.TransferRequest;
import gigabank.service.kafka.MetricsService;
import gigabank.service.kafka.TransactionProducer;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import gigabank.service.BankAccountService;

import java.util.LinkedHashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final BankAccountService bankAccountService;
    private final TransactionProducer transactionProducer;
    private final MetricsService metricsService;

    public TransactionController(BankAccountService bankAccountService, TransactionProducer transactionProducer, MetricsService metricsService) {
        this.bankAccountService = bankAccountService;
        this.transactionProducer = transactionProducer;
        this.metricsService = metricsService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid transfer request");
        }
        bankAccountService.transfer(
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount()
        );
        return ResponseEntity.ok("Successfully transferred");
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generate(@RequestParam int count,
                                                        @RequestParam(defaultValue = "at-least-once") String mode) {
        if (count <= 0 || count > 1000) {
            return ResponseEntity.badRequest().body(Map.of("mode", mode, "message", "Illegal request"));
        }

        switch (mode) {
            case "at-most-once" -> transactionProducer.sendAtMostOnce(count);
            case "at-least-once" -> transactionProducer.sendAtLeastOnce(count);
            case "exactly-once" -> transactionProducer.sendExactlyOnce(count);
            default -> throw new IllegalArgumentException("Unknown mode: " + mode);
        }

        return ResponseEntity.accepted().body(Map.of(
                "mode", mode,
                "message", "Successfully generated transactions"
        ));
    }

    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        Map<String, Object> map = new LinkedHashMap<>();
        metricsService.snapshot().forEach((mode, m) -> {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("processed", m.getProcessed().sum());
            data.put("errors", m.getErrors().sum());
            data.put("retries", m.getRetries().sum());
            data.put("duplicates", m.getDuplicates().sum());
            data.put("batches", m.getBatches().sum());
            data.put("avgBatchTimeMs", m.avgBatchTimeMs());
            data.put("avgSingleTimeMs", m.avgSingleTimeMs());
            map.put(mode, data);
        });
        return map;
    }
}
