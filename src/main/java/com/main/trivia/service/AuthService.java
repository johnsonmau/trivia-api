package com.main.trivia.service;

import com.main.trivia.model.User;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<?> register(User user);
    ResponseEntity<?> login(User user);
    void logout(long userId);
    ResponseEntity<?> deleteAccount(String token);
    ResponseEntity<?> validateLogin(String token);
}
