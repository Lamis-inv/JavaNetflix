package org.JavaProject.JavaNetflixProject.Entities;

import java.util.List;

public class UserEntities {
	private int id;
    private String nom;
    private String email;
    private String motDePasseHash;
    private String role; // USER ou ADMIN
    private List<FilmEntities> listeFavoris;
    private List<VisionnageEntities> historiqueVisionnage;
    
	public UserEntities() {
		super();
	}
	public UserEntities(int id, String nom, String email, String motDePasseHash, String role,
			List<FilmEntities> listeFavoris, List<VisionnageEntities> historiqueVisionnage) {
		super();
		this.id = id;
		this.nom = nom;
		this.email = email;
		this.motDePasseHash = motDePasseHash;
		this.role = role;
		this.listeFavoris = listeFavoris;
		this.historiqueVisionnage = historiqueVisionnage;
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
	public String getMotDePasseHash() {
		return motDePasseHash;
	}
	public void setMotDePasseHash(String motDePasseHash) {
		this.motDePasseHash = motDePasseHash;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public List<FilmEntities> getListeFavoris() {
		return listeFavoris;
	}
	public void setListeFavoris(List<FilmEntities> listeFavoris) {
		this.listeFavoris = listeFavoris;
	}
	public List<VisionnageEntities> getHistoriqueVisionnage() {
		return historiqueVisionnage;
	}
	public void setHistoriqueVisionnage(List<VisionnageEntities> historiqueVisionnage) {
		this.historiqueVisionnage = historiqueVisionnage;
	}
    
    
}
