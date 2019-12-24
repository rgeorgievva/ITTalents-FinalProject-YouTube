package model.playlist;

import java.time.LocalDateTime;

public class Playlist {
    private int    id;
    private String title;
    private LocalDateTime date_created;
    private int           owner_id;

    public Playlist(int id, String title, LocalDateTime date_created, int owner_id) {
        this.id = id;
        this.title = title;
        this.date_created = date_created;
        this.owner_id = owner_id;
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

    public LocalDateTime getDate_created() {
        return date_created;
    }

    public void setDate_created(LocalDateTime date_created) {
        this.date_created = date_created;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }
}
