package com.inventaris.auth.domain;

public class Admin extends User {
    public Admin(String name, String password) {
        super(name, password, Role.ADMIN);
    }

    @Override
    public void tampilkanMenu() {
        MenuLauncher launcher = getMenuLauncher();
        if (launcher != null) {
            launcher.launchAdminMenu();
        }
    }
}