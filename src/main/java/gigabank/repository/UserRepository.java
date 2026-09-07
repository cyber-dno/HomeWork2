package gigabank.repository;

import gigabank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> getUserByEmail_AndName(String email, String name);
}
