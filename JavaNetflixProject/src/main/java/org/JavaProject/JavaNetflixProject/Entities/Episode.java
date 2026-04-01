package org.JavaProject.JavaNetflixProject.Entities;

public class Episode {
    private int id;
    private int seasonId;
    private int episodeNum;
    private String title;
    private String synopsis;
    private int durationMin;
    private String videoUrl;
    private String thumbnailUrl;

    // Watch progress (populated for logged-in user)
    private boolean watched;
    private int progressSec;

    public Episode() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSeasonId() { return seasonId; }
    public void setSeasonId(int seasonId) { this.seasonId = seasonId; }
    public int getEpisodeNum() { return episodeNum; }
    public void setEpisodeNum(int episodeNum) { this.episodeNum = episodeNum; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }
    public int getDurationMin() { return durationMin; }
    public void setDurationMin(int durationMin) { this.durationMin = durationMin; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public boolean isWatched() { return watched; }
    public void setWatched(boolean watched) { this.watched = watched; }
    public int getProgressSec() { return progressSec; }
    public void setProgressSec(int progressSec) { this.progressSec = progressSec; }

    @Override public String toString() { return "E" + episodeNum + " - " + title; }
}
