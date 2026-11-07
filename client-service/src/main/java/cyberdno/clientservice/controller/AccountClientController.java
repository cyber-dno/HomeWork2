package cyberdno.clientservice.controller;

import cyberdno.clientservice.dto.Account;
import cyberdno.clientservice.exception.AccountNotFoundException;
import cyberdno.clientservice.exception.ServiceException;
import cyberdno.clientservice.service.AccountClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/client/accounts")
public class AccountClientController {

    private final AccountClientService accountClientService;

    public AccountClientController(AccountClientService accountClientService) {
        this.accountClientService = accountClientService;
    }

//    ### Создание аккаунта
//    POST http://localhost:8081/api/client/accounts
//    Content-Type: application/json
//
//    {
//        "id": 2,
//        "balance": 500
//    }
//
//    ### Получение аккаунта
//    GET http://localhost:8081/api/client/accounts/2
//
//    ### Получение не существующего аккаунта
//    GET http://localhost:8081/api/client/accounts/100

    @GetMapping("/{id}")
    public Mono<Account> getAccount(@PathVariable Long id) {
        return accountClientService.getAccount(id);
    }

    @PostMapping
    public Mono<Account> createAccount(@RequestBody Account account) {
        return accountClientService.createAccount(account);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleNotFound(AccountNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<String> handleService(ServiceException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
    }
}
