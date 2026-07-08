package entity;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BankAccountBuilder {
    private BankAccount bankAccount;

    public BankAccountBuilder reset() {
        bankAccount = new BankAccount();
        return this;
    }

    public BankAccountBuilder  setNumber(String number) {
        bankAccount.setNumber(number);
        return this;
    }

    public BankAccountBuilder  setBalance(BigDecimal balance) {
        bankAccount.setBalance(balance);
        return this;
    }

    public BankAccountBuilder  setUser(User user) {
        bankAccount.setUser(user);
        return this;
    }
}
