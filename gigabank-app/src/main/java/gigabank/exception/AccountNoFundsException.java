package gigabank.exception;

public class AccountNoFundsException extends RuntimeException {
    public AccountNoFundsException(String accountNumber) {
        super(String.format("На счету '%s' недостаточно средств", accountNumber));
    }
}
