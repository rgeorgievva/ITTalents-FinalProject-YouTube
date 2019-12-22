package model.comment;

import java.time.LocalDateTime;

public class Comment {
    private int    id;
    private String text;
    private LocalDateTime time_posted;
    private int  video_id;
    private int owner_id;
    private int replied_to_id;


    public Comment(String text, int owner_id) {
        this.text = text;
        this.owner_id = owner_id;
        this.time_posted = LocalDateTime.now();
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

    public LocalDateTime getTime_posted() {
        return time_posted;
    }

    public void setTime_posted(LocalDateTime time_posted) {
        this.time_posted = time_posted;
    }

    public int getVideo_id() {
        return video_id;
    }

    public void setVideo_id(int video_id) {
        this.video_id = video_id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public int getReplied_to_id() {
        return replied_to_id;
    }

    public void setReplied_to_id(int replied_to_id) {
        this.replied_to_id = replied_to_id;
    }
}
