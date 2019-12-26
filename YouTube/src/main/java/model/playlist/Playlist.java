package model.playlist;

import java.time.LocalDateTime;

public class Playlist {
    private int    id;
    private String        title;
    private LocalDateTime dateCreated;
    private int           ownerId;

    public Playlist(int id, String title, LocalDateTime dateCreated, int ownerId) {
        this.id = id;
        this.title = title;
        this.dateCreated = dateCreated;
        this.ownerId = ownerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
