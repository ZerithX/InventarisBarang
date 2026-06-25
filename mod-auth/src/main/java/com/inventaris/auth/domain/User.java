package com.inventaris.auth.domain;

//import com.inventaris.coreshared.AuthException;

import java.util.UUID;

public class User {
    private String id;
    private String name;
    private String password;
    private Role role;

    public User(String name, String password, Role role) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

}