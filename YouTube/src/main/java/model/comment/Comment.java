package model.comment;

import java.time.LocalDateTime;

public class Comment {
    private int    id;
    private String        text;
    private LocalDateTime timePosted;
    private int videoId;
    private int ownerId;
    private int repliedToId;


    public Comment(String text, int ownerId) {
        this.text = text;
        this.ownerId = ownerId;
        this.timePosted = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(LocalDateTime timePosted) {
        this.timePosted = timePosted;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getRepliedToId() {
        return repliedToId;
    }

    public void setRepliedToId(int repliedToId) {
        this.repliedToId = repliedToId;
    }
}
