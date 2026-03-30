package org.JavaProject.JavaNetflixProject.Entities;

public class NoteEntities {
	private int id;
    private int valeur; // entre 1 et 5
    private User auteur;
    private Film film;
    
	public NoteEntities(int id, int valeur, User auteur, Film film) {
		super();
		this.id = id;
		this.valeur = valeur;
		this.auteur = auteur;
		this.film = film;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getValeur() {
		return valeur;
	}

	public void setValeur(int valeur) {
		this.valeur = valeur;
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
    
    
	

}
