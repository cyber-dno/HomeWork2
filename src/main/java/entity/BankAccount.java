package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    private String id;
    private BigDecimal balance;
    private User owner;
    private List<Transaction> transactions = new ArrayList<>();

    public BankAccount(String id, List<Transaction> transactions) {
        this.id = id;
        this.transactions = transactions;
    }
}
