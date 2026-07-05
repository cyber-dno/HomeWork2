package dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    @NotNull(message = "Счёт отправителя должен быть заполнен")
    private Integer fromAccountId;

    @NotNull(message = "Счёт получателя должен быть заполнен")
    private Integer toAccountId;

    @DecimalMin(value = "0.0", message = "Сумма перевода должна быть больше или равна {value}")
    @NotNull(message = "Сумма перевода не должна быть пустой")
    private BigDecimal amount;
}
