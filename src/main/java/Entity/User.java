package Entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate birthDate;
    private List<BankAccount> bankAccounts;

    public User() {
        this.id = "1";
        this.firstName = "John";
        this.middleName = "Doe";
        this.lastName = "Calm";
        this.birthDate = LocalDate.now();
        this.bankAccounts = new ArrayList<>();
    }
}
