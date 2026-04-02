package org.JavaProject.JavaNetflixProject.Entities;

import java.time.LocalDateTime;

public class Content {
    public enum Type { FILM, SERIE }

    private int id;
    private String title;
    private Type type;
    private String synopsis;
    private int releaseYear;
    private String coverUrl;
    //private String bannerUrl;
    private String trailerUrl;
    private String videoUrl;     // films only
    private int durationMin;     // films only
    private String casting;
    private Category category;
    private double avgRating;
    private int viewCount;
    private boolean featured;
    private LocalDateTime createdAt;

    public Content() {}

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }
    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    
    /*public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    */
    public String getTrailerUrl() { return trailerUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public int getDurationMin() { return durationMin; }
    public void setDurationMin(int durationMin) { this.durationMin = durationMin; }
    public String getCasting() { return casting; }
    public void setCasting(String casting) { this.casting = casting; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isFilm() { return type == Type.FILM; }
    public boolean isSerie() { return type == Type.SERIE; }

    @Override public String toString() { return title + " (" + releaseYear + ")"; }
}
