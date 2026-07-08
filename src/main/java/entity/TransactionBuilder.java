package entity;

import classifier.TransactionType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TransactionBuilder {
    private Transaction transaction;

    public TransactionBuilder  reset() {
        transaction = new Transaction();
        return this;
    }

    public TransactionBuilder  setId(String id) {
        transaction.setId(id);
        return this;
    }

    public TransactionBuilder  setAmount(BigDecimal value) {
        transaction.setAmount(value);
        return this;
    }

    public TransactionBuilder  setType(TransactionType type) {
        transaction.setType(type);
        return this;
    }

    public TransactionBuilder  setDate(LocalDateTime createdDate) {
        transaction.setDate(createdDate);
        return this;
    }

    public TransactionBuilder  setUser(User user) {
        transaction.setUser(user);
        return this;
    }

    public TransactionBuilder  setSource(User source) {
        transaction.setSource(source);
        return this;
    }

    public TransactionBuilder  setTarget(User target) {
        transaction.setTarget(target);
        return this;
    }

}
