package com.main.trivia.service;

import com.main.trivia.model.Error;
import com.main.trivia.model.User;
import com.main.trivia.repository.UserRepository;
import com.main.trivia.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String registerValidationRes;
    private String loginValidationRes;

    @Override
    public ResponseEntity<?> register(User user) {

/*        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Username is required\"}.\"}");
        }*/

        if (validUser(user)) {
            System.out.println("user");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok("User registered successfully");
        }

        return ResponseEntity.badRequest().body(new Error(registerValidationRes));
    }

    private boolean validUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            registerValidationRes = "Username is required";
            return false;
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            registerValidationRes = "password is required";
            return false;
        }

        if (user.getCountryCd() == null || user.getCountryCd().trim().isEmpty()) {
            registerValidationRes = "country cd is required";
            return false;
        }

        if (user.getUsername().length() < 5) {
            registerValidationRes = "Username must be at least 5 characters";
            return false;
        }

        if (user.getCountryCd().length() > 2) {
            registerValidationRes = "Country code cant be greater than 2 characters";
            return false;
        }

        if (user.getUsername().length() > 10) {
            registerValidationRes = "Username cant be greater than 10 characters";
            return false;
        }

        if (user.getPassword().length() < 6) {
            registerValidationRes = "Password must be at least 6 characters";
            return false;
        }

        if (user.getUsername().contains(" ")) {
            registerValidationRes = "Username cannot contain spaces";
            return false;
        }

        if (user.getPassword().contains(" ")) {
            registerValidationRes = "Password cannot contain spaces";
            return false;
        }

        if (userRepository.findByUsername(user.getUsername()) != null) {
            registerValidationRes = "User with that name already exists";
            return false;
        }

        return true;
    }

    @Override
    public ResponseEntity<?> login(User user) {

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            loginValidationRes = "{\"error\": \"Username is required\"}";
            return ResponseEntity.badRequest().body(new Error(loginValidationRes));

        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            loginValidationRes = "{\"error\": \"Username is required\"}";
            return ResponseEntity.badRequest().body(new Error(loginValidationRes));
        }

        // Authenticate user
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.status(401).body(new Error("Invalid username or password"));
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(existingUser.getUsername());

        existingUser.updateLastActive();
        userRepository.save(existingUser);

        // Return the token in the response
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Override
    public void logout(String token) {
        String username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        User user = userRepository.findByUsername(username);
        user.logout(); // Updates `active` and `lastActive`
        userRepository.save(user);
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteAccount(String token) {

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new Error("unauthorized"));
        }

        String username = null;

        try {
            username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new Error("cant parse auth token"));
        }

        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            return ResponseEntity.badRequest().body(new Error("user doesn't exist"));
        }

        userRepository.deleteByUsername(username);

        return ResponseEntity.ok("User account deleted successfully");
    }

    @Override
    public ResponseEntity<?> getUserDetails(String token) {

        User user;

        try {
            String username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
            user = userRepository.findByUsername(username);
        } catch (ExpiredJwtException ex){
            return ResponseEntity.status(401).body(new Error("invalid token"));
        }

        if (user == null || !user.isActive()) {
            return ResponseEntity.status(401).body(new Error("user is logged out or invalid token"));
        }

        return ResponseEntity.ok(user);
    }

}
