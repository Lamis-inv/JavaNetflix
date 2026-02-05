package org.JavaProject.JavaNetflixProject.Entities;

public class EpisodeEntities {
	private int id;
    private String titre;
    private int numero;
    private String urlVideo;
    private int duree; 
    private String resume;
    
	public EpisodeEntities(int id, String titre, int numero, String urlVideo, int duree, String resume) {
		super();
		this.id = id;
		this.titre = titre;
		this.numero = numero;
		this.urlVideo = urlVideo;
		this.duree = duree;
		this.resume = resume;
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

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getUrlVideo() {
		return urlVideo;
	}

	public void setUrlVideo(String urlVideo) {
		this.urlVideo = urlVideo;
	}

	public int getDuree() {
		return duree;
	}

	public void setDuree(int duree) {
		this.duree = duree;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}
    
    

}
