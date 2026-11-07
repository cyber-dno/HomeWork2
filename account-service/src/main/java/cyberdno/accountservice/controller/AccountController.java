package cyberdno.accountservice.controller;

import cyberdno.accountservice.model.Account;
import cyberdno.accountservice.repository.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

//    ### Создание аккаунта
//    POST http://localhost:8082/api/accounts
//    Content-Type: application/json
//    Authorization: Basic admin admin123
//
//    {
//        "id": 1,
//        "balance": 1000
//    }
//
//    ### Получение аккаунта
//    GET http://localhost:8082/api/accounts/1
//    Authorization: Basic admin admin123
//
//    ### Получение аккаунта без авторизации
//    GET http://localhost:8082/api/accounts/1
//
//    ### Получение статуса сервиса
//    GET http://localhost:8082/actuator/health
//    Authorization: Basic admin admin123
//
//    ### Получение метрик сервиса
//    GET http://localhost:8082/actuator/prometheus
//    Authorization: Basic admin admin123

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        return accountRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountRepository.save(account);
    }
}
