import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class Chatroom {

    private String name;
    private HashMap<String, String> userAndMessages = new HashMap<>();

    public Chatroom(String name) {
        this.name = name;
    }

    public HashMap<String, String> getUserAndMessages() {
        return userAndMessages;
    }

    public void addUserMessages(String key, String value) {
        userAndMessages.put(key, value);
    }

    public String getName() {
        return name;
    }

}
