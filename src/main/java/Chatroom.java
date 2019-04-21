import java.util.HashMap;

class Chatroom {

    private String name;
    private HashMap<String, String> userAndMessages = new HashMap<>();

    Chatroom(String name) {
        this.name = name;
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

}
