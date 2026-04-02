package org.JavaProject.JavaNetflixProject.Entities;

import java.util.ArrayList;
import java.util.List;

public class Season {
    private int id;
    private int serieId;
    private int number;
    private String title;
    private List<Episode> episodes = new ArrayList<>();

    public Season() {}
    public Season(int id, int serieId, int number, String title) {
        this.id = id; this.serieId = serieId; this.number = number; this.title = title;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSerieId() { return serieId; }
    public void setSerieId(int serieId) { this.serieId = serieId; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<Episode> getEpisodes() { return episodes; }
    public void setEpisodes(List<Episode> episodes) { this.episodes = episodes; }

    @Override public String toString() { return title != null ? title : "Saison " + number; }
}
