package ai.api.sample.model;

/**
 * Created by anuj on 14/07/18.
 */
public class Message {
    private String message;
    private int viewType;

    public Message(String message, int viewType) {
        this.message = message;
        this.viewType = viewType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
