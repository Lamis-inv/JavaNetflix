package org.JavaProject.JavaNetflixProject.Utils;

import org.JavaProject.JavaNetflixProject.Entities.User;

/**
 * Holds the currently logged-in user for the session.
 */
public class SessionManager {
    private static User currentUser;

    private SessionManager() {}

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }
    public static void logout() { currentUser = null; }
    public static boolean isLoggedIn() { return currentUser != null; }
    public static boolean isAdmin() { return isLoggedIn() && currentUser.isAdmin(); }
}
