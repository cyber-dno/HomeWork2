package gigabank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    @NotNull(message = "Счёт отправителя должен быть заполнен")
    private String fromAccountNumber;

    @NotNull(message = "Счёт получателя должен быть заполнен")
    private String toAccountNumber;

    @DecimalMin(value = "0.0", message = "Сумма перевода должна быть больше или равна {amount}")
    @NotNull(message = "Сумма перевода не должна быть пустой")
    private BigDecimal amount;
}
