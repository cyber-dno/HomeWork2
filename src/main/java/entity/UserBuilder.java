package entity;

import lombok.Getter;

@Getter
public class UserBuilder {
    private User user;

    public UserBuilder reset() {
        user = new User();
        return this;
    }

    public UserBuilder  setId(String id) {
        user.setId(id);
        return this;
    }

    public UserBuilder  setName(String name) {
        user.setName(name);
        return this;
    }

    public UserBuilder  setEmail(String email) {
        user.setEmail(email);
        return this;
    }

    public UserBuilder  setPhoneNumber(String phone) {
        user.setPhoneNumber(phone);
        return this;
    }
}
