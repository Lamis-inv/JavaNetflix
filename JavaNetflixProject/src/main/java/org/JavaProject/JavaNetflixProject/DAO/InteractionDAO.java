package org.JavaProject.JavaNetflixProject.DAO;

import org.JavaProject.JavaNetflixProject.Entities.CommentaireEntities;
import org.JavaProject.JavaNetflixProject.Entities.NoteEntities;
import org.JavaProject.JavaNetflixProject.Entities.UserEntities;
import org.JavaProject.JavaNetflixProject.Utils.ConxDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InteractionDAO {
/*
    Connection cnx = ConxDB.getInstance().getCnx();

    // ------------------- FAVORIS -------------------

    public boolean isFavori(int userId, int filmId) {
        String req = "SELECT * FROM favoris WHERE user_id = ? AND film_id = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            ResultSet rs = ps.executeQuery();
            return rs.next(); 
        } catch (SQLException e) {
            System.out.println("Erreur isFavori : " + e.getMessage());
        }
        return false;
    }

    public void toggleFavori(int userId, int filmId) {
        if (isFavori(userId, filmId)) {
            supprimerFavori(userId, filmId);
        } else {
            ajouterFavori(userId, filmId);
        }
    }

    public void ajouterFavori(int userId, int filmId) {
        String req = "INSERT INTO favoris (user_id, film_id) VALUES (?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            ps.executeUpdate();
            System.out.println("Film ajouté aux favoris !");
        } catch (SQLException e) {
            System.out.println("Erreur ajout favori : " + e.getMessage());
        }
    }

    public void supprimerFavori(int userId, int filmId) {
        String req = "DELETE FROM favoris WHERE user_id = ? AND film_id = ?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            ps.executeUpdate();
            System.out.println("Film retiré des favoris !");
        } catch (SQLException e) {
            System.out.println("Erreur suppression favori : " + e.getMessage());
        }
    }

 

    public void ajouterCommentaire(CommentaireEntities commentaire) {
        String req = "INSERT INTO commentaires (contenu, date_pub, user_id, film_id, signale) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, commentaire.getContenu());
            ps.setTimestamp(2, Timestamp.valueOf(commentaire.getDate()));
            ps.setInt(3, commentaire.getAuteur().getId());
            ps.setInt(4, commentaire.getFilm().getId());
            ps.setBoolean(5, false);

            ps.executeUpdate();
            System.out.println("Commentaire ajouté !");
        } catch (SQLException e) {
            System.out.println("Erreur ajout commentaire : " + e.getMessage());
        }
    }

    public List<CommentaireEntities> getCommentairesByFilmId(int filmId) {
        List<CommentaireEntities> liste = new ArrayList<>();
        
     
        String req = "SELECT c.*, u.username FROM commentaires c JOIN user u ON c.user_id = u.id WHERE c.film_id = ? ORDER BY c.date_pub DESC";
        
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, filmId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
              
                CommentaireEntities cm = new CommentaireEntities();
                cm.setId(rs.getInt("id"));
                cm.setContenu(rs.getString("contenu"));
                cm.setDate(rs.getTimestamp("date_pub").toLocalDateTime());
                
                // Création de l'auteur
                UserEntities user = new UserEntities(); 
                user.setId(rs.getInt("user_id"));
               
                user.setNom(rs.getString("username")); 
                
                cm.setAuteur(user);
                
                liste.add(cm);
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération commentaires : " + e.getMessage());
        }
        return liste;
    }

    // ------------------- NOTES (RATING) -------------------

    public void ajouterNote(NoteEntities note) {
        // Vérifie si l'utilisateur a déjà noté
        String checkReq = "SELECT id FROM notes WHERE user_id = ? AND film_id = ?";
        try {
            PreparedStatement psCheck = cnx.prepareStatement(checkReq);
            psCheck.setInt(1, note.getAuteur().getId());
            psCheck.setInt(2, note.getFilm().getId());
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                // UPDATE (Mise à jour)
                String updateReq = "UPDATE notes SET valeur = ? WHERE user_id = ? AND film_id = ?";
                PreparedStatement psUpdate = cnx.prepareStatement(updateReq);
                psUpdate.setInt(1, note.getValeur());
                psUpdate.setInt(2, note.getAuteur().getId());
                psUpdate.setInt(3, note.getFilm().getId());
                psUpdate.executeUpdate();
            } else {
                // INSERT (Nouvelle note)
                String insertReq = "INSERT INTO notes (valeur, user_id, film_id) VALUES (?, ?, ?)";
                PreparedStatement psInsert = cnx.prepareStatement(insertReq);
                psInsert.setInt(1, note.getValeur());
                psInsert.setInt(2, note.getAuteur().getId());
                psInsert.setInt(3, note.getFilm().getId());
                psInsert.executeUpdate();
            }
            System.out.println("Note enregistrée !");
        } catch (SQLException e) {
            System.out.println("Erreur ajout note : " + e.getMessage());
        }
    }
    
    */
}