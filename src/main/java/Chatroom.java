import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Chatroom {

    private String name;
    private Path path;
    private List<Message> messageList = new ArrayList<>();
    private HashMap<String, List<Message>> userAndMessages = new HashMap<>();

    Chatroom(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void addToMessageList(Message message) {
        messageList.add(message);
    }

    HashMap<String, List<Message>> getUserAndMessages() {
        return userAndMessages;
    }

    void addUserToChatroom(String key) {
        userAndMessages.put(key, messageList);
    }

    void addMessageToUser(String key, Message value) {
        userAndMessages.get(key).add(value);
    }

    String getName() {
        return name;
    }

    Path getPath() {
        return path;
    }
}
