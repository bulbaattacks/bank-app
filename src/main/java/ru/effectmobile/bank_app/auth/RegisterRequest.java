package ru.effectmobile.bank_app.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.effectmobile.bank_app.entity.User;

@Getter
@Setter
@Builder
public class RegisterRequest {
    private String email;
    private String password;
    private User.Role role;
}
