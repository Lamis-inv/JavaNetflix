package org.JavaProject.JavaNetflixProject.Entities;

import java.time.LocalDateTime;

public class VisionnageEntities {
	 private int id;
	    private User user;
	    private Film film; // ou Episode selon le cas
	    private double progression; // 0 à 100 (%)
	    private LocalDateTime dateDerniereVision;
		public VisionnageEntities(int id, User user, Film film, double progression,
				LocalDateTime dateDerniereVision) {
			super();
			this.id = id;
			this.user = user;
			this.film = film;
			this.progression = progression;
			this.dateDerniereVision = dateDerniereVision;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public User getUser() {
			return user;
		}
		public void setUser(User user) {
			this.user = user;
		}
		public Film getFilm() {
			return film;
		}
		public void setFilm(Film film) {
			this.film = film;
		}
		public double getProgression() {
			return progression;
		}
		public void setProgression(double progression) {
			this.progression = progression;
		}
		public LocalDateTime getDateDerniereVision() {
			return dateDerniereVision;
		}
		public void setDateDerniereVision(LocalDateTime dateDerniereVision) {
			this.dateDerniereVision = dateDerniereVision;
		}
	    
	    

}
