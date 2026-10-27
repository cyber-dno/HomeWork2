package gigabank.controller;

import gigabank.dto.CreateAccountRequest;
import gigabank.dto.DepositWithdrawRequest;
import gigabank.dto.ErrorResponse;
import gigabank.model.BankAccount;
import gigabank.model.Transaction;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import gigabank.service.BankAccountService;

import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    private static ErrorResponse getErrors(BindingResult result) {
        List<String> errors = result.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return new ErrorResponse(
                400,
                "Validation failed",
                errors
        );
    }

    @GetMapping("")
    public ResponseEntity<?> getAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {
        return ResponseEntity.ok(bankAccountService.getAccounts(page, size));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> closeAccount(@PathVariable String id) {
        return ResponseEntity.ok(bankAccountService.closeAccount(id));
    }

    @PostMapping("")
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest,
                                           BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(BankAccountController.getErrors(result));
        }
        BankAccount account = bankAccountService.addBankAccount(createAccountRequest);
        return ResponseEntity.ok(account);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getAccount(@PathVariable("id") String number) {
        BankAccount account = bankAccountService.findAccountByNumber(number);
        return ResponseEntity.ok(account);
    }

    @PostMapping("{id}/deposit")
    public ResponseEntity<?> depositAccount(@PathVariable("id") String number,
                                               @Valid @RequestBody DepositWithdrawRequest depositWithdrawRequest,
                                               BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(BankAccountController.getErrors(result));
        }
        BankAccount account = bankAccountService.deposit(number, depositWithdrawRequest.getAmount());
        return ResponseEntity.ok(account);
    }

    @PostMapping("{id}/withdraw")
    public ResponseEntity<?> withdrawAccount(@PathVariable("id")  String number,
                                                @Valid @RequestBody DepositWithdrawRequest depositWithdrawRequest,
                                                BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(BankAccountController.getErrors(result));
        }
        BankAccount account = bankAccountService.withdraw(number, depositWithdrawRequest.getAmount());
        return ResponseEntity.ok(account);
    }

    @GetMapping("{id}/transactions")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable("id")  String number) {
        List<Transaction> transactions = bankAccountService.getAccountTransactions(number);
        return ResponseEntity.ok(transactions);
    }
}
