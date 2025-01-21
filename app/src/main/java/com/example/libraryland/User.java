package com.example.libraryland;

public class User {
    private String name;
    private String email;
    private String role;
    private String username;
    private String key; // Pour stocker la clé Firebase si nécessaire

    // Constructeur vide requis par Firebase
    public User() {}

    // Constructeur pour créer un objet utilisateur
    public User(String name, String email, String role, String username) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.username = username;
    }

    // Getters et setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
