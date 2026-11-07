package gigabank.service.entity.user;

import gigabank.model.User;
import gigabank.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable(value = "userCache", key = "'id:' + #userId", unless = "#result == null")
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    @CachePut(value = "userCache", key = "'id:' + #result.id")
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @CacheEvict(value = "userCache", key = "'id:' + #userId")
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @CachePut(value = "userCache", key = "'id:' + #result.id")
    public User findOrCreate(String name, String email) {
        User user = userRepository.getUserByEmail_AndName(email, name).orElse(null);
        if (user != null) {
            return user;
        }
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        return userRepository.save(newUser);
    }
}
