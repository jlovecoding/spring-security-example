package com.example.securityex.controller;

import com.example.securityex.exception.BadRequestException;
import com.example.securityex.exception.NotFoundException;
import com.example.securityex.model.User;
import com.example.securityex.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static java.lang.String.format;

@RestController("/users")
public class UserController {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;


    public UserController(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public User getUser(@RequestParam final String email) {
        return userRepository.findById(email)
                .orElseThrow(() -> new NotFoundException(format("Email %s does not exist", email)));
    }

    @PostMapping
    public User create(@RequestBody final User user) {
        if (userRepository.findById(user.getEmail()).isPresent()) {
            throw new BadRequestException(format("Email %s already exists", user.getEmail()));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

}
