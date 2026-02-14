package org.JavaProject.JavaNetflixProject.Entities;

import java.time.LocalDate;
import java.util.List;


public class FilmEntities {
	private int id;
    private String titre;
    private String description;
    private String genre;
    private String urlVideo;
    private String coverUrl;
    private LocalDate dateSortie;
    private List<NoteEntities> notes;
    private List<CommentaireEntities> commentaires;
	public FilmEntities(int id, String titre, String description, String genre, String urlVideo, String coverUrl,
			LocalDate dateSortie, List<NoteEntities> notes, List<CommentaireEntities> commentaires) {
		super();
		this.id = id;
		this.titre = titre;
		this.description = description;
		this.genre = genre;
		this.urlVideo = urlVideo;
		this.coverUrl = coverUrl;
		this.dateSortie = dateSortie;
		this.notes = notes;
		this.commentaires = commentaires;
	}
	
	
	
	public FilmEntities(int id, String titre, String description, String genre,
			LocalDate dateSortie,String urlVideo, String coverUrl) {
		super();
		this.id = id;
		this.titre = titre;
		this.description = description;
		this.genre = genre;
		this.urlVideo = urlVideo;
		this.coverUrl = coverUrl;
		this.dateSortie = dateSortie;
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
	public String getDescription() {
		return description;
	}

	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getUrlVideo() {
		return urlVideo;
	}
	public void setUrlVideo(String urlVideo) {
		this.urlVideo = urlVideo;
	}
	public String getCoverUrl() {
		return coverUrl;
	}
	
	public LocalDate getDateSortie() {
		return dateSortie;
	}
	public void setDateSortie(LocalDate dateSortie) {
		this.dateSortie = dateSortie;
	}
	public List<NoteEntities> getNotes() {
		return notes;
	}
	public void setNotes(List<NoteEntities> notes) {
		this.notes = notes;
	}
	public List<CommentaireEntities> getCommentaires() {
		return commentaires;
	}
	public void setCommentaires(List<CommentaireEntities> commentaires) {
		this.commentaires = commentaires;
	}
    
    
}
