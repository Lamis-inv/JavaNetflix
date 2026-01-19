package org.JavaProject.JavaNetflixProject.Entities;

public class NoteEntities {
	private int id;
    private int valeur; // entre 1 et 5
    private UserEntities auteur;
    private FilmEntities film;
    
	public NoteEntities(int id, int valeur, UserEntities auteur, FilmEntities film) {
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
    
    
	

}
