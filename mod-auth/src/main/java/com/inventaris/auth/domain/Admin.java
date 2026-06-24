package com.inventaris.auth.domain;

public class Admin extends User {
    public Admin(String name, String password) {
        super(name, password, Role.ADMIN);
    }
}