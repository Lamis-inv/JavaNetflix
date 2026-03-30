package org.JavaProject.JavaNetflixProject.Services;

import java.sql.SQLException;

import org.JavaProject.JavaNetflixProject.DAO.UserDAO;
import org.JavaProject.JavaNetflixProject.Entities.User;
import org.JavaProject.JavaNetflixProject.Utils.Session;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class AuthService {
	
    private final static UserDAO userDAO = new UserDAO();

    public static User login(String email, String password) throws SQLException {
        if (email == null || email.isBlank() || password == null || password.isBlank())
            throw new IllegalArgumentException("Email et mot de passe requis.");

        User user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null) throw new IllegalArgumentException("Aucun compte trouvé avec cet email.");

        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        if (!result.verified) throw new IllegalArgumentException("Mot de passe incorrect.");

        Session.setCurrentUser(user);
        return user;
    }

    public User register(String name, String email, String password, String confirmPassword) throws SQLException {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Le nom est requis.");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Email invalide.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
        if (!password.equals(confirmPassword))
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        if (userDAO.emailExists(email.trim().toLowerCase()))
            throw new IllegalArgumentException("Cet email est déjà utilisé.");

        String hashed = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        User user = new User();
        user.setNom(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(hashed);
        user.setRole("USER");
        return userDAO.save(user);
    }

    public void logout() {
        Session.logout();
    }
}
