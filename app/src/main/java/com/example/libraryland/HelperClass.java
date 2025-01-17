package com.example.libraryland;

public class HelperClass {
    String name, email, username, password, role;

    public HelperClass() {
    }

    public HelperClass(String name, String email, String username, String password, String role) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role; // Ajout du rôle
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
