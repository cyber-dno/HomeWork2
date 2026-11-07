package gigabank;

import gigabank.model.User;
import gigabank.service.entity.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class UserCacheIntegrationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserServiceImpl userService;

    @Test
    void userShouldCacheActions () {
        User created = userService.findOrCreate("Test User", "test@example.com");
        String key = "userCache::id:" + created.getId();

        assertNotNull(redisTemplate.opsForValue().get(key));

        User loaded = userService.getUserById(created.getId());
        assertNotNull(loaded);
        assertNotNull(redisTemplate.opsForValue().get(key));

        userService.deleteUser(created.getId());
        assertNull(redisTemplate.opsForValue().get(key));
    }
}
