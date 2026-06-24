package com.inventaris.auth.domain;

public class Staff extends User {
    public Staff(String name, String password) {
        super(name, password, Role.STAFF);
    }
}