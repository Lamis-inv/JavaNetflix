package org.JavaProject.JavaNetflixProject.Entities;

import java.time.LocalDateTime;
import java.util.List;

public class User {
	private int id;
    private String nom;
    private String email;
    private String password;
    private String role; // USER ou ADMIN
    private String avatarUrl;
    private LocalDateTime createdAt;
    
	public User() {
		super();
	}
	public User(int id, String nom, String email, String password, String role,
			String avatarUrl, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.nom = nom;
		this.email = email;
		this.password = password;
		this.role = role;
		this.avatarUrl = avatarUrl;
		this.createdAt = createdAt;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return this.role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean isAdmin() { return "ADMIN".equals(role); }
	public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getPassword() {
    	return password; 
    }
    public void setPassword(String password) {
    	this.password = password; 
    }
    
    
}
