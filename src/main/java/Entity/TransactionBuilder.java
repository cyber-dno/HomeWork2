package Entity;

import Enum.TransactionType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TransactionBuilder {
    private Transaction transaction;

    public void reset() {
        transaction = new Transaction();
    }

    public void setId(String id) {
        transaction.setId(id);
    }

    public void setValue(BigDecimal value) {
        transaction.setValue(value);
    }

    public void setType(TransactionType type) {
        transaction.setType(type);
    }

    public void setCategory(String category) {
        transaction.setCategory(category);
    }

    public void setBankAccount(BankAccount bankAccount) {
        transaction.setBankAccount(bankAccount);
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        transaction.setCreatedDate(createdDate);
    }

    public void setMerchantName(String merchantName) {
        transaction.setMerchantName(merchantName);
    }

    public void setMerchantCategoryCode(String merchantCategoryCode) {
        transaction.setMerchantCategoryCode(merchantCategoryCode);
    }

    public void setCardNumber(String cardNumber) {
        transaction.setCardNumber(cardNumber);
    }

    public void setBankName(String bankName) {
        transaction.setBankName(bankName);
    }

    public void setDigitalWalletId(String digitalWalletId) {
        transaction.setDigitalWalletId(digitalWalletId);
    }

}
