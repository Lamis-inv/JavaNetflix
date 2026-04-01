package org.JavaProject.JavaNetflixProject.Entities;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private int userId;
    private String userName;
    private int contentId;
    private String body;
    private boolean flagged;
    private LocalDateTime createdAt;

    public Comment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public int getContentId() { return contentId; }
    public void setContentId(int contentId) { this.contentId = contentId; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
