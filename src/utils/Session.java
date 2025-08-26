package utils;

import models.User;

public class Session {
    private static User currentUser;

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static boolean isLogged() {
        return currentUser != null;
    }

    public static void clear() {
        currentUser = null;
    }
}
