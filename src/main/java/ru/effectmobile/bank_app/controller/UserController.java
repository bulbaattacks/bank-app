package ru.effectmobile.bank_app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.effectmobile.bank_app.entity.User;
import ru.effectmobile.bank_app.exception.EntityNotFoundException;
import ru.effectmobile.bank_app.repository.UserRepository;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") Long id, Authentication auth) {
        var authenticatedUser = ((User) auth.getPrincipal());
        if (authenticatedUser.getRole() != User.Role.ADMIN && !authenticatedUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    }
}
