public class Message {

    private long timestamp;
    private String author;
    private String message;

    public Message(long timestamp, String author, String message) {
        this.timestamp = timestamp;
        this.author = author;
        this.message = message;
    }

    String getAuthor() {
        return author;
    }

    long getTimestamp() {
        return timestamp;
    }

    String getMessage() {
        return message;
    }

}
