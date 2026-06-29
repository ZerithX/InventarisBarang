package com.inventaris.auth.domain;

public class Staff extends User {
    public Staff(String name, String password) {
        super(name, password, Role.STAFF);
    }

    @Override
    public void tampilkanMenu() {
        MenuLauncher launcher = getMenuLauncher();
        if (launcher != null) {
            launcher.launchStaffMenu();
        }
    }
}