package org.JavaProject.JavaNetflixProject.Utils;

import org.JavaProject.JavaNetflixProject.Entities.User;

public class Session{
    private static User currentUser;

    private Session() {}

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }
    public static void logout() { currentUser = null; }
    public static boolean isLoggedIn() { return currentUser != null; }
    public static boolean isAdmin() { return isLoggedIn() && currentUser.isAdmin(); }
}
