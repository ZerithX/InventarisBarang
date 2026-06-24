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

    public void setName(String name) {
//        if (name == null || name.trim().isEmpty()) {
//            throw new IlegalArgumentException("Nama tidak boleh kosong");
//        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPassword(String password) {
//        if  (password == null || password.trim().isEmpty()) {
//            throw new IlegalArgumentException("Password tidak boleh kosong")
//        }
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

//    public void setRole(Role role) {
//        return role;
//    }

    public Role getRole() {
        return role;
    }

}