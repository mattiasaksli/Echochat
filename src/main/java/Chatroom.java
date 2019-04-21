import java.nio.file.Path;
import java.util.HashMap;

class Chatroom {

    private String name;
    private Path path;
    private HashMap<String, String> userAndMessages = new HashMap<>();

    Chatroom(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    HashMap<String, String> getUserAndMessages() {
        return userAndMessages;
    }

    void addUserMessages(String key, String value) {
        userAndMessages.put(key, value);
    }

    void replaceUserMessages(String key, String value) {
        userAndMessages.replace(key, value);
    }

    String getName() {
        return name;
    }

    Path getPath() {
        return path;
    }
}
