package gigabank.service.entity.account;

import gigabank.exception.AccountNotFoundException;
import gigabank.model.BankAccount;
import gigabank.repository.BankAccountRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    private final BankAccountRepository bankAccountRepository;

    public AccountServiceImpl(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    @Cacheable(value = "accountsCache", key = "'user:' + #userId")
    public List<BankAccount> getUserAccounts(Long userId) {
        return bankAccountRepository.getBankAccountsByUser_Id(userId);
    }

    @Override
    @Transactional
    @Caching(
            put = @CachePut(value = "accountsCache", key = "'number:' + #result.accountNumber"),
            evict = @CacheEvict(value = "accountsCache", key = "'user:' + #result.user.id")
    )
    public BankAccount createAccount(BankAccount account) {
        return bankAccountRepository.save(account);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "accountsCache", key = "'number:' + #result.accountNumber"),
            @CacheEvict(value = "accountsCache", key = "'user:' + #result.user.id"),
            @CacheEvict(value = "transactionsCache", key = "'account:' + #result.id"),
            @CacheEvict(value = "transactionsCache", key = "'number:' + #result.accountNumber")
    })
    public BankAccount closeAccount(Long id) {
        BankAccount account = bankAccountRepository.getBankAccountById(id);
        if (account == null) {
            throw new AccountNotFoundException(id);
        }
        bankAccountRepository.deleteById(id);
        return account;
    }

    @Override
    public Page<BankAccount> getAccounts(int page, int size) {
        return bankAccountRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    @Cacheable(value = "accountsCache", key = "'number:' + #number")
    public BankAccount findAccountByNumber(String number) {
        BankAccount account = bankAccountRepository.getBankAccountByAccountNumber(number);
        if (account == null) {
            throw new AccountNotFoundException(number);
        }
        return account;
    }

    @Override
    @Transactional
    @Caching(
            put = @CachePut(value = "accountsCache", key = "'number:' + #result.accountNumber"),
            evict = @CacheEvict(value = "accountsCache", key = "'user:' + #result.user.id")
    )
    public BankAccount updateAccount(BankAccount account) {
        return bankAccountRepository.save(account);
    }
}
