package org.JavaProject.JavaNetflixProject.Entities;

import java.util.List;

public class SaisonEntities {
	private int id;
    private String titre;
    private String genre;
    private List<String> acteursGlobaux;
    private List<SaisonEntities> saisons;
	public SaisonEntities(int id, String titre, String genre, List<String> acteursGlobaux,
			List<SaisonEntities> saisons) {
		super();
		this.id = id;
		this.titre = titre;
		this.genre = genre;
		this.acteursGlobaux = acteursGlobaux;
		this.saisons = saisons;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public List<String> getActeursGlobaux() {
		return acteursGlobaux;
	}
	public void setActeursGlobaux(List<String> acteursGlobaux) {
		this.acteursGlobaux = acteursGlobaux;
	}
	public List<SaisonEntities> getSaisons() {
		return saisons;
	}
	public void setSaisons(List<SaisonEntities> saisons) {
		this.saisons = saisons;
	}
    
    
}
