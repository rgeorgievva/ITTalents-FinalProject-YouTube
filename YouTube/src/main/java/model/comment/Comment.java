package model.comment;

import java.time.LocalDateTime;

public class Comment {
    private long id;
    private String text;
    private LocalDateTime time_posted;
    private long video_id;
    private long owner_id;
    private long replied_to_id;

    public Comment(String text, long video_id, long owner_id) {
        this.text = text;
        this.video_id = video_id;
        this.owner_id = owner_id;
    }

    public Comment(String text, LocalDateTime time_posted, long video_id, long owner_id, long replied_to_id) {
       this(text, video_id, owner_id);
        this.replied_to_id = replied_to_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getVideo_id() {
        return video_id;
    }

    public void setVideo_id(long video_id) {
        this.video_id = video_id;
    }

    public long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(long owner_id) {
        this.owner_id = owner_id;
    }

    public long getReplied_to_id() {
        return replied_to_id;
    }

    public void setReplied_to_id(long replied_to_id) {
        this.replied_to_id = replied_to_id;
    }
}
