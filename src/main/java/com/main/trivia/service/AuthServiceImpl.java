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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

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
        logger.info("Registering user with username: {}", user.getUsername());

        if (validUser(user)) {
            logger.info("User {} registered successfully", user.getUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok("User registered successfully");
        }

        logger.error("User registration failed: {}", registerValidationRes);
        return ResponseEntity.badRequest().body(new Error(registerValidationRes));
    }

    private boolean validUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            registerValidationRes = "Username is required";
            return false;
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            registerValidationRes = "Password is required";
            return false;
        }

        if (user.getCountryCd() == null || user.getCountryCd().trim().isEmpty()) {
            registerValidationRes = "Country code is required";
            return false;
        }

        if (user.getCountryCd().length() > 2) {
            registerValidationRes = "Country code can't be greater than 2 characters";
            return false;
        }

        if (user.getUsername().length() > 10) {
            registerValidationRes = "Username can't be greater than 10 characters";
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
        logger.info("Attempting login for username: {}", user.getUsername());

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            loginValidationRes = "{\"error\": \"Username is required\"}";
            logger.error("Login failed: {}", loginValidationRes);
            return ResponseEntity.badRequest().body(new Error(loginValidationRes));
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            loginValidationRes = "{\"error\": \"Password is required\"}";
            logger.error("Login failed: {}", loginValidationRes);
            return ResponseEntity.badRequest().body(new Error(loginValidationRes));
        }

        // Authenticate user
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            logger.error("Login failed: Invalid username or password for {}", user.getUsername());
            return ResponseEntity.status(401).body(new Error("Invalid username or password"));
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(existingUser.getUsername());
        existingUser.updateLastActive();
        userRepository.save(existingUser);

        logger.info("User {} logged in successfully, token generated", user.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Override
    public void logout(String token) {
        String username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        User user = userRepository.findByUsername(username);
        if (user != null) {
            logger.info("Logging out user: {}", username);
            user.logout(); // Updates `active` and `lastActive`
            userRepository.save(user);
        } else {
            logger.warn("Logout attempt failed: User not found with token username");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteAccount(String token) {

        if (token == null || token.trim().isEmpty()) {
            logger.error("Unauthorized delete account attempt: Token is null or empty");
            return ResponseEntity.badRequest().body(new Error("unauthorized"));
        }

        String username = null;
        try {
            username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        } catch (Exception ex) {
            logger.error("Failed to parse authorization token", ex);
            return ResponseEntity.badRequest().body(new Error("can't parse auth token"));
        }

        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            logger.error("Delete account failed: User does not exist {}", username);
            return ResponseEntity.badRequest().body(new Error("user doesn't exist"));
        }

        userRepository.deleteByUsername(username);
        logger.info("User {} account deleted successfully", username);
        return ResponseEntity.ok("User account deleted successfully");
    }

    @Override
    public ResponseEntity<?> getUserDetails(String token) {
        User user;

        try {
            String username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
            user = userRepository.findByUsername(username);
        } catch (ExpiredJwtException ex) {
            logger.error("Token expired for user {}", token);
            return ResponseEntity.status(401).body(new Error("invalid token"));
        }

        if (user == null || !user.isActive()) {
            logger.warn("User not found or not active for token: {}", token);
            return ResponseEntity.status(401).body(new Error("user is logged out or invalid token"));
        }

        logger.info("Fetched user details for username: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }
}
