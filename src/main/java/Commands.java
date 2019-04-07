import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Commands {

    static void writeEnd(DataOutputStream socketOut) throws Exception {
        socketOut.writeInt(MessageTypes.END_SESSION.getValue());
    }

    static void writeUpdateRequest(DataOutputStream socketOut) throws Exception {
        socketOut.writeInt(MessageTypes.UPDATE_REQ.getValue());
    }

    static void writeUserToMap(DataOutputStream socketOut, String username) throws Exception {
        socketOut.writeInt(MessageTypes.USER_MAP.getValue());
        socketOut.writeUTF(username);
    }

    static void messageAuthor(DataOutputStream socketOut, String username) throws Exception {
        socketOut.writeInt(MessageTypes.AUTHOR_SIGNATURE.getValue());
        socketOut.writeUTF(username);
    }

    static void writeMessage(DataOutputStream socketOut, String message, int type, boolean isRequest) throws Exception {
        if (isRequest) {
            socketOut.writeInt(type);
            socketOut.writeUTF(message);
        } else {
            if (type == MessageTypes.TEXT.getValue()) {
                socketOut.writeInt(type);
                socketOut.writeUTF(message);
            }
        }
    }

    static int getType(DataInputStream socketIn) throws Exception {
        return socketIn.readInt();
    }

    static String getUsername(DataInputStream socketIn) throws Exception {
        return socketIn.readUTF();
    }

    static String readMessage(DataInputStream socketIn, int type) throws Exception {

        if (type == MessageTypes.END_SESSION.getValue() ||
                type == MessageTypes.UPDATE_REQ.getValue()) {
            return "";
        } else if (type == MessageTypes.TEXT.getValue() ||
                type == MessageTypes.USER_MAP.getValue() ||
                type == MessageTypes.AUTHOR_SIGNATURE.getValue()) {
            return processMessage1(socketIn);
        } else {
            throw new IllegalArgumentException("type " + type);
        }
    }

    static String processMessage1(DataInputStream value) throws Exception {
        return value.readUTF();
    }
}
