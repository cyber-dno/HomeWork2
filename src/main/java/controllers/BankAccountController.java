package controllers;

import dto.CreateAccountRequest;
import dto.DepositWithdrawRequest;
import dto.ErrorResponse;
import entity.BankAccount;
import entity.Transaction;
import entity.User;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.BankAccountService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/accounts")
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

    @PostMapping("")
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest,
                                                     BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(BankAccountController.getErrors(result));
        }
        BankAccount account = new BankAccount(
                "1",
                createAccountRequest.getInitialBalance(),
                new User(
                        "1",
                        createAccountRequest.getOwnerName(),
                        createAccountRequest.getOwnerEmail(),
                        createAccountRequest.getOwnerPhone()
                ),
                new ArrayList<>()
        );
        return ResponseEntity.ok(account);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getAccount(@PathVariable("id") Integer id) {
        BankAccount account = bankAccountService.findAccountById(id);
        return ResponseEntity.ok(account);
    }

    @PostMapping("{id}/deposit")
    public ResponseEntity<?> depositAccount(@PathVariable("id") Integer id,
                                               @Valid @RequestBody DepositWithdrawRequest depositWithdrawRequest,
                                               BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(BankAccountController.getErrors(result));
        }
        BankAccount account = bankAccountService.deposit(id, depositWithdrawRequest.getAmount());
        return ResponseEntity.ok(account);
    }

    @PostMapping("{id}/withdraw")
    public ResponseEntity<?> withdrawAccount(@PathVariable("id") Integer id,
                                                @Valid @RequestBody DepositWithdrawRequest depositWithdrawRequest,
                                                BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(BankAccountController.getErrors(result));
        }
        BankAccount account = bankAccountService.withdraw(id, depositWithdrawRequest.getAmount());
        return ResponseEntity.ok(account);
    }

    @GetMapping("{id}/transactions")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable("id") Integer id) {
        BankAccount account = bankAccountService.findAccountById(id);
        List<Transaction> transactions = account.getTransactions();
        return ResponseEntity.ok(transactions);
    }
}
