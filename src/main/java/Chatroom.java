import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Chatroom {

    private String name;
    private Path path;
    private List<Message> messageList;
    private List<FileMessage> fileMessageList;
    private HashMap<String, List<Message>> userAndMessages;
    private HashMap<String, List<FileMessage>> userAndFiles;

    Chatroom(String name, Path path) {
        this.name = name;
        this.path = path;
        this.messageList = new ArrayList<>();
        this.userAndMessages = new HashMap<>();
        this.userAndFiles = new HashMap<>();
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    void addToMessageList(Message message) {
        messageList.add(message);
    }

    HashMap<String, List<Message>> getUserAndMessages() {
        return userAndMessages;
    }

    HashMap<String, List<FileMessage>> getUserAndFiles() {
        return userAndFiles;
    }

    void addUserToChatroom(String key) {
        userAndMessages.put(key, new ArrayList<>(messageList));
        userAndFiles.put(key, new ArrayList<>());
    }

    void addMessageToUser(String key, Message value) {
        userAndMessages.get(key).add(value);
    }

    void addFileToUser(String key, FileMessage value) {
        userAndFiles.get(key).add(value);
    }

    String getName() {
        return name;
    }

    Path getPath() {
        return path;
    }
}
