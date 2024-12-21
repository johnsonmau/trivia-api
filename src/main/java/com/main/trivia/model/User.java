package com.main.trivia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // This makes it writable but not readable
    private String password;

    @Column(nullable = false)
    @JsonProperty("country")
    private String countryCd;

    @ElementCollection(fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<String> roles = new HashSet<>();

    @Column(nullable = false)
    @JsonIgnore
    private boolean active = true; // Represents login/logout state

    @Column(nullable = false)
    @JsonIgnore
    private int gamesPlayed = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("date DESC")
    private List<Score> scores = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    @JsonIgnore
    private LocalDateTime dateCreated = LocalDateTime.now();

    @Column(nullable = false)
    @JsonIgnore
    private LocalDateTime lastActive = LocalDateTime.now();

    public User() {
        roles.add("ROLE_USER"); // Default role
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountryCd() {
        return countryCd;
    }

    public void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    // Add a method to increment games played
    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    public void updateLastActive() {
        this.lastActive = LocalDateTime.now();
    }

    // Add a method to add a score
    public void addScore(Score score) {
        this.scores.add(score);
        score.setUser(this); // Set the relationship in the Score entity
    }

    // Add a method to handle logout
    public void logout() {
        this.active = false;
        updateLastActive();
    }

    // Add a method to handle login
    public void login() {
        this.active = true;
        updateLastActive();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", countryCd='" + countryCd + '\'' +
                ", roles=" + roles +
                ", active=" + active +
                ", gamesPlayed=" + gamesPlayed +
                ", scores=" + scores +
                ", dateCreated=" + dateCreated +
                ", lastActive=" + lastActive +
                '}';
    }
}

