package gigabank.service.entity.user;

import gigabank.model.User;

public interface UserService {
    User getUserById(Long userId);

    User updateUser(User user);

    void deleteUser(Long userId);

    User findOrCreate(String name, String email);
}
