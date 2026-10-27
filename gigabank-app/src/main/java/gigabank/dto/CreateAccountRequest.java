package gigabank.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    @Size(min = 3, max = 30, message = "Имя должно находиться в диапазоне от {min} до {max}")
    private String ownerName;

    @Email(message = "Почта не соответствует стандартам Email")
    private String ownerEmail;

    @Pattern(regexp = "\\+[0-9]{10,15}", message = "Некорректный формат номера")
    private String ownerPhone;

    @DecimalMin(value = "0.0", message = "Начальный баланс должен быть больше или равен {amount}")
    @NotNull(message = "Начальный баланс не должен быть пустым")
    private BigDecimal initialBalance;
}
