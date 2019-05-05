public class FileMessage {

    private long timestamp;
    private String author;
    private byte[] file;
    private String fileName;

    public FileMessage(long timestamp, String author, byte[] file, String fileName) {
        this.timestamp = timestamp;
        this.author = author;
        this.file = file;
        this.fileName = fileName;
    }

    String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    byte[] getFile() {
        return file;
    }

    public void setMessage(byte[] file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }
}
