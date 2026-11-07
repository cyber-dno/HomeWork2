package gigabank.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super(String.format("Счет с номером '%s' не найден", accountNumber));
    }

    public AccountNotFoundException(Long accountId) {
        super(String.format("Счет с ID '%d' не найден", accountId));
    }
}
