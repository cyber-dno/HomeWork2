package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Refund {
    private int id;
    private BigDecimal amount;
    private String description;
    private Transaction transaction;
}