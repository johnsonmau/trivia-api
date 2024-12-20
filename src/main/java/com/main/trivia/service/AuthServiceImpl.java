package com.main.trivia.service;

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
    private String deleteValidationRes;

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

        return ResponseEntity.badRequest().body(registerValidationRes);
    }

    private boolean validUser(User user) {
        System.out.println(user);
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            registerValidationRes = "{\"error\": \"Username is required\"}";
            return false;
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            registerValidationRes = "{\"error\": \"password is required\"}";
            return false;
        }

        if (user.getUsername().length() < 5) {
            registerValidationRes = "{\"error\": \"Username must be at least 5 characters\"}";
            return false;
        }

        if (user.getPassword().length() < 6) {
            registerValidationRes = "{\"error\": \"Password must be at least 6 characters\"}";
            return false;
        }

        if (user.getUsername().contains(" ")) {
            registerValidationRes = "{\"error\": \"Username cannot contain spaces\"}";
            return false;
        }

        if (user.getPassword().contains(" ")) {
            registerValidationRes = "{\"error\": \"Password cannot contain spaces\"}";
            return false;
        }

        if (userRepository.findByUsername(user.getUsername()) != null) {
            System.out.println("user exists");
            registerValidationRes = "{\"error\": \"User with that name already exists\"}";
            return false;
        }

        return true;
    }

    @Override
    public ResponseEntity<?> login(User user) {

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            loginValidationRes = "{\"error\": \"Username is required\"}.\"}";
            return ResponseEntity.badRequest().body(loginValidationRes);

        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            loginValidationRes = "{\"error\": \"Username is required\"}.\"}";
            return ResponseEntity.badRequest().body(loginValidationRes);
        }

        // Authenticate user
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(existingUser.getUsername());

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
            deleteValidationRes = "{\"error\": \"unauthorized\"}.\"}";
            return ResponseEntity.badRequest().body(deleteValidationRes);
        }

        String username = null;

        try {
            username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        } catch (Exception ex) {
            deleteValidationRes = "{\"error\": \"cant parse auth token\"}.\"}";
            return ResponseEntity.badRequest().body(deleteValidationRes);
        }

        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            deleteValidationRes = "{\"error\": \"user doesn't exist\"}.\"}";
            return ResponseEntity.badRequest().body(deleteValidationRes);
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
            System.out.println("Token is expired");
            return ResponseEntity.status(401).body("Invalid token");
        }

        if (user == null || !user.isActive()) {
            return ResponseEntity.status(401).body("User is logged out or invalid token");
        }

        return ResponseEntity.ok(user);
    }

}
