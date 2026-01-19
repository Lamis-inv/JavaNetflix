package org.JavaProject.JavaNetflixProject.Entities;

import java.time.LocalDate;
import java.util.List;


public class FilmEntities {
	private int id;
    private String titre;
    private String synopsis;
    private String genre;
    private String urlVideo;
    private String urlImage;
    private LocalDate dateSortie;
    private List<NoteEntities> notes;
    private List<CommentaireEntities> commentaires;
	public FilmEntities(int id, String titre, String synopsis, String genre, String urlVideo, String urlImage,
			LocalDate dateSortie, List<NoteEntities> notes, List<CommentaireEntities> commentaires) {
		super();
		this.id = id;
		this.titre = titre;
		this.synopsis = synopsis;
		this.genre = genre;
		this.urlVideo = urlVideo;
		this.urlImage = urlImage;
		this.dateSortie = dateSortie;
		this.notes = notes;
		this.commentaires = commentaires;
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
	public String getSynopsis() {
		return synopsis;
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
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
	public String getUrlImage() {
		return urlImage;
	}
	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
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
