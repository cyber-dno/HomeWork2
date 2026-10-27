import gigabank.model.UserTest;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Arrays;

public class PojoTest {
    @Test
    public void testWithGeneratedPojo() throws Exception {
        // Читаем JSON из ресурсов
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("user.json");

        // Создаем ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Преобразуем JSON в POJO
        UserTest user = objectMapper.readValue(inputStream, UserTest.class);

        // Используем данные
        System.out.println("User ID: " + user.getId());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Roles: " + user.getRoles());
        System.out.println("Active: " + user.getActive());

        // Пример создания нового объекта
        UserTest newUser = new UserTest();
        newUser.setId(2);
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setRoles(Arrays.asList("USER", "EDITOR"));

        // Преобразуем обратно в JSON
        String userJson = objectMapper.writeValueAsString(newUser);
        System.out.println("User as JSON: " + userJson);
    }
}
