package com.example.hbv501g.Persistence.Entities;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long user_id;

    private String username;
    private String password;
    private String email;

    private List<String> lookedAt;

    private boolean loggedIn;

    @Override
    public int hashCode() {
        return Objects.hash(user_id);
    }

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.loggedIn = false;
    }

    public long getUserId() {
        return user_id;
    }

    public void setUserId(long user_id) {
        this.user_id = user_id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public List<String> getLookedAt() {
        if (lookedAt == null) {
            lookedAt = new ArrayList<>();
        }
        return lookedAt;
    }
    
    public void setLookedAt(List<String> lookedAt) {
        this.lookedAt = lookedAt;
    }
    
    public void addLookedAtCategory(String category) {
        if (lookedAt == null) {
            lookedAt = new ArrayList<>();
        }
        lookedAt.add(category);
    }
    
}
