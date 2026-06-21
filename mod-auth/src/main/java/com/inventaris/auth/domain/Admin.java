package com.inventaris.auth.domain;

class Admin extends User {
    public Admin(String name, String password) {
        super(username, password, Role.ADMIN);
    }
}