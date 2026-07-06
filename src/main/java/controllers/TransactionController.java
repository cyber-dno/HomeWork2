package controllers;

import dto.TransferRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.db.BankAccountService;

@Validated
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final BankAccountService bankAccountService;

    public TransactionController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid transfer request");
        }
        bankAccountService.transfer(
                request.getFromAccountNumber(),
                request.getToAccountNumber(),
                request.getAmount()
        );
        return ResponseEntity.ok("Successfully transferred");
    }
}
