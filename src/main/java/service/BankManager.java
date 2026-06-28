package service;

import dto.UserRequest;
import entity.BankAccount;
import entity.Refund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankManager {
    private final BankAccountService serv;
    private final RefundService refundService;

    public BankManager(BankAccountService bankAccountService,
                       RefundService refundService) {
        this.serv = bankAccountService;
        this.refundService = refundService;
    }

    public void doWork(List<UserRequest> rqs) {
        serv.createTestAccount();
        for (UserRequest r : rqs) {
            BankAccount a = serv.findAccountById(r.getAccountId());
            if (a == null) {
                System.out.println("no acc " + r.getAccountId());
                refundService.createRefund(new Refund());
                continue;
            }
            switch (r.getPaymentType()) {
                case "CARD" -> {
                    serv.processCardPayment(a, r.getAmount());
                    System.out.println("\n");
                }
                case "BANK" -> {
                    serv.processBankTransfer(a, r.getAmount());
                    System.out.println("\n");
                }
                case "WALLET" -> {
                    serv.processWalletPayment(a, r.getAmount());
                    System.out.println("\n");
                }
            }
        }
    }
}