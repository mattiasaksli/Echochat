class FileMessage {

    private long timestamp;
    private String author;
    private byte[] file;
    private String fileName;

    FileMessage(long timestamp, String author, byte[] file, String fileName) {
        this.timestamp = timestamp;
        this.author = author;
        this.file = file;
        this.fileName = fileName;
    }

    byte[] getFile() {
        return file;
    }
}
