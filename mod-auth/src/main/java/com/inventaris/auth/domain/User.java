package com.inventaris.auth.domain;

import java.util.UUID;

public abstract class User {
    private String id;
    private String name;
    private String password;
    private Role role;
    private static MenuLauncher menuLauncher;

    public User(String name, String password, Role role) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public static void setMenuLauncher(MenuLauncher launcher) {
        menuLauncher = launcher;
    }

    protected static MenuLauncher getMenuLauncher() {
        return menuLauncher;
    }

    public abstract void tampilkanMenu();

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