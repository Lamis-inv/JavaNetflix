package org.JavaProject.JavaNetflixProject.Entities;

import java.time.LocalDateTime;

public class Comment {
	private int id;
    private String contenu;
    private User auteur;
    private Film film;
    private LocalDateTime date;
    private boolean signale;
    
    
    
	public Comment() {
		super();
	}
	public Comment(int id, String contenu, User auteur, Film film, LocalDateTime date,
			boolean signale) {
		super();
		this.id = id;
		this.contenu = contenu;
		this.auteur = auteur;
		this.film = film;
		this.date = date;
		this.signale = signale;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContenu() {
		return contenu;
	}
	public void setContenu(String contenu) {
		this.contenu = contenu;
	}
	public User getAuteur() {
		return auteur;
	}
	public void setAuteur(User auteur) {
		this.auteur = auteur;
	}
	public Film getFilm() {
		return film;
	}
	public void setFilm(Film film) {
		this.film = film;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public boolean isSignale() {
		return signale;
	}
	public void setSignale(boolean signale) {
		this.signale = signale;
	}
    

}
