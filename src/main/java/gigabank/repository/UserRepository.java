package gigabank.repository;

import gigabank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> getUserByEmail_AndName(String email, String name);

    Optional<User> getUserByUsername(String username);

    Optional<User> findByUsername(String username);
}
