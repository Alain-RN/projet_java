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

    public static boolean isAdmin(User user) {
        return "admin".equals(user.getUsername())
                && "1234".equals(user.getPassword())
                && user.getId() == 1;
    }
}
