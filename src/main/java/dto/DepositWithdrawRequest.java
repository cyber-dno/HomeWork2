package dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositWithdrawRequest {
    @DecimalMin(value = "0.0", message = "Сумма активности должна быть больше или равна {amount}")
    @NotNull(message = "Сумма активности не должна быть пустой")
    private BigDecimal amount;
}
