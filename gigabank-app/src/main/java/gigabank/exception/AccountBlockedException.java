package gigabank.exception;

public class AccountBlockedException extends RuntimeException {
    public AccountBlockedException(String accountNumber) {
        super(String.format("Счет '%s' заблокирован", accountNumber));
    }
}
