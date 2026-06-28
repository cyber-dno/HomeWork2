package entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class User {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private List<BankAccount> bankAccounts = new ArrayList<>();

    public User(String id, String firstName, String email, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
