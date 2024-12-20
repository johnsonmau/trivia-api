package com.main.trivia.service;

import com.main.trivia.model.User;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<?> register(User user);
    ResponseEntity<?> login(User user);
    void logout(String token);
    ResponseEntity<?> deleteAccount(String token);
    ResponseEntity<?> getUserDetails(String token);
}
