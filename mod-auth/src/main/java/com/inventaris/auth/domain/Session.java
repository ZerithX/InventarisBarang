package com.inventaris.auth.domain;

public class Session {
    private static User loggedInUser;

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void clear() {
        loggedInUser = null;
    }
}
