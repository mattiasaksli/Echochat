import java.io.DataInputStream;
import java.io.DataOutputStream;

class Commands {

    static void writeEnd(DataOutputStream socketOut) throws Exception {
        socketOut.writeInt(MessageTypes.EXIT_CHATROOM.value());
    }

    static void writeUpdateRequest(DataOutputStream socketOut) throws Exception {
        socketOut.writeInt(MessageTypes.UPDATE_REQ.value());
    }

    static void writeFileUpdateRequest(DataOutputStream socketOut) throws Exception {
        socketOut.writeInt(MessageTypes.FILE_UPDATE_REQ.value());
    }

    static void writeChatroomName(DataOutputStream socketOut, String chatroomName) throws Exception {
        socketOut.writeInt(MessageTypes.CHATROOM_CONNECT_USER.value());
        socketOut.writeUTF(chatroomName);
    }

    static void writeFile(DataOutputStream socketOut, String fileName, byte[] file) throws Exception {
        socketOut.writeInt(MessageTypes.SEND_FILE.value());
        socketOut.writeInt(file.length);
        socketOut.write(file);
        socketOut.writeUTF(fileName);
    }

    static byte[] readFile(DataInputStream dataIn) throws Exception {
        int length = dataIn.readInt();
        return dataIn.readNBytes(length);
    }

    static void writeMessage(DataOutputStream socketOut, String message, int type, boolean isRequest) throws Exception {
        if (isRequest) {
            socketOut.writeInt(type);
            socketOut.writeUTF(message);

        } else {
            if (type == MessageTypes.TEXT.value()) {
                socketOut.writeInt(type);
                socketOut.writeUTF(message);

            }
        }
    }

    static int getType(DataInputStream socketIn) throws Exception {
        return socketIn.readInt();
    }

    static String readMessage(DataInputStream socketIn, int type) throws Exception {

        if (type == MessageTypes.END_SESSION.value() ||
                type == MessageTypes.EXIT_CHATROOM.value() ||
                type == MessageTypes.UPDATE_REQ.value()) {
            return "";
        } else if (type == MessageTypes.TEXT.value()) {
            return socketIn.readUTF();
        } else {
            throw new IllegalArgumentException("type " + type);
        }
    }
}