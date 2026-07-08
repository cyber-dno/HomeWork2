package controllers;

import db.DBConnectionManager;
import dto.CreateAccountRequest;
import dto.DepositWithdrawRequest;
import dto.ErrorResponse;
import entity.BankAccount;
import entity.Transaction;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.db.BankAccountServiceBD;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/accounts")
public class BankAccountController {
    private final BankAccountServiceBD bankAccountService;

    public BankAccountController(BankAccountServiceBD bankAccountService) {
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
        Connection connection = DBConnectionManager.getInstance().get();
        BankAccount account = bankAccountService.addBankAccount(createAccountRequest, connection);
        DBConnectionManager.getInstance().disconnect(connection);
        return ResponseEntity.ok(account);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getAccount(@PathVariable("id") String number) {
        Connection connection = DBConnectionManager.getInstance().get();
        BankAccount account = bankAccountService.findAccountByNumber(number, connection);
        DBConnectionManager.getInstance().disconnect(connection);
        return ResponseEntity.ok(account);
    }

    @PostMapping("{id}/deposit")
    public ResponseEntity<?> depositAccount(@PathVariable("id") String number,
                                               @Valid @RequestBody DepositWithdrawRequest depositWithdrawRequest,
                                               BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(BankAccountController.getErrors(result));
        }
        Connection connection = DBConnectionManager.getInstance().get();
        BankAccount account = bankAccountService.deposit(number, depositWithdrawRequest.getAmount(), connection);
        DBConnectionManager.getInstance().disconnect(connection);
        return ResponseEntity.ok(account);
    }

    @PostMapping("{id}/withdraw")
    public ResponseEntity<?> withdrawAccount(@PathVariable("id")  String number,
                                                @Valid @RequestBody DepositWithdrawRequest depositWithdrawRequest,
                                                BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(BankAccountController.getErrors(result));
        }
        Connection connection = DBConnectionManager.getInstance().get();
        BankAccount account = bankAccountService.withdraw(number, depositWithdrawRequest.getAmount(), connection);
        DBConnectionManager.getInstance().disconnect(connection);
        return ResponseEntity.ok(account);
    }

    @GetMapping("{id}/transactions")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable("id")  String number) {
        Connection connection = DBConnectionManager.getInstance().get();
        List<Transaction> transactions = bankAccountService.getAccountTransactions(number, connection);
        DBConnectionManager.getInstance().disconnect(connection);
        return ResponseEntity.ok(transactions);
    }
}
