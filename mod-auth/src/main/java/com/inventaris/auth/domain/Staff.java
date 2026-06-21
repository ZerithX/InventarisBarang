package com.inventaris.auth.domain;

class Staff extends User {
    public Staff(String name, String password) {
        super(name, password, Role.STAFF);
    }
}