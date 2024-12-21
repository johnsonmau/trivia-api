package com.main.trivia.controller;

import com.main.trivia.annotation.RateLimit;
import com.main.trivia.model.User;
import com.main.trivia.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*") // Allow requests from any origin
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @RateLimit
    public ResponseEntity<?> register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    @RateLimit
    public @ResponseBody ResponseEntity<?> login(@RequestBody User user, HttpServletRequest request) {
        return authService.login(user);
    }

    @PostMapping("/logout/{userId}")
    public void logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(@RequestHeader("Authorization") String token) {
        return authService.deleteAccount(token);
    }

    @GetMapping("/user/details")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String token) {
        return authService.getUserDetails(token);
    }

}
