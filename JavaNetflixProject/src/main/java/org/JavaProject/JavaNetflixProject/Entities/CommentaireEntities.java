package org.JavaProject.JavaNetflixProject.Entities;

import java.time.LocalDateTime;

public class CommentaireEntities {
	private int id;
    private String contenu;
    private UserEntities auteur;
    private FilmEntities film;
    private LocalDateTime date;
    private boolean signale;
    
    
    
	public CommentaireEntities() {
		super();
	}
	public CommentaireEntities(int id, String contenu, UserEntities auteur, FilmEntities film, LocalDateTime date,
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
	public UserEntities getAuteur() {
		return auteur;
	}
	public void setAuteur(UserEntities auteur) {
		this.auteur = auteur;
	}
	public FilmEntities getFilm() {
		return film;
	}
	public void setFilm(FilmEntities film) {
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
