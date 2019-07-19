package common;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mgiles on 6/13/14.
 */
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 345915723355497705L;
    private String message;
    private String author;
    private long time;
    private boolean isMe;

    public ChatMessage() {
        this("", "");
    }

    public ChatMessage(String author, String message) {
        this.author = author;
        this.message = message;
        this.time = new Date().getTime();
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
