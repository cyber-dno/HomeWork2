package gigabank.service.entity.account;

import gigabank.model.BankAccount;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AccountService {
    List<BankAccount> getUserAccounts(Long userId);

    BankAccount createAccount(BankAccount account);

    BankAccount  closeAccount(Long id);

    Page<BankAccount> getAccounts(int page, int size);

    BankAccount findAccountByNumber(String number);

    BankAccount updateAccount(BankAccount account);
}
