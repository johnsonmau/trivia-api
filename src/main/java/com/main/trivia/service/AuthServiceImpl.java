package com.main.trivia.service;

import com.main.trivia.model.User;
import com.main.trivia.repository.UserRepository;
import com.main.trivia.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<?> register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @Override
    public ResponseEntity<?> login(User user) {
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
    public void logout(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.logout(); // Updates `active` and `lastActive`
        userRepository.save(user);
    }



    @Override
    @Transactional
    public ResponseEntity<?> deleteAccount(String token) {
        String username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        userRepository.deleteByUsername(username);
        return ResponseEntity.ok("User account deleted successfully");
    }

    @Override
    public ResponseEntity<?> getUserDetails(String token) {
        String username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
        User user = userRepository.findByUsername(username);

        if (user == null || !user.isActive()) {
            return ResponseEntity.status(401).body("User is logged out or invalid token");
        }
        return ResponseEntity.ok(user);
    }

}
