import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Commands {

    static void writeEnd(DataOutputStream socketOut) throws Exception {
        socketOut.writeInt(-1);
    }

    static void writeUpdateRequest(DataOutputStream socketOut) throws Exception {
        socketOut.writeInt(5);
    }

    static void writeUserToMap(DataOutputStream socketOut, String username) throws Exception {
        socketOut.writeInt(6);
        socketOut.writeUTF(username);
    }

    static void messageAuthor(DataOutputStream socketOut, String username) throws Exception {
        socketOut.writeInt(7);
        socketOut.writeUTF(username);
    }

    static void writeMessage(DataOutputStream socketOut, String message, int messageType, boolean isRequest) throws Exception {
        if (isRequest) {
            socketOut.writeInt(messageType);
            socketOut.writeUTF(message);

        } else {
            if (messageType == 1) {
                socketOut.writeInt(messageType);
                socketOut.writeUTF(message);

            } else {
                if (checkFile(message) == 3) {
                    socketOut.writeInt(3);
                    System.out.println("sent non-server-relative path error");

                } else if (checkFile(message) == 2) {
                    socketOut.writeInt(2);
                    socketOut.writeInt(processFile(message).length);
                    socketOut.write(processFile(message));
                    System.out.println("file on its way!");

                } else if (messageType == 10) {
                    socketOut.writeInt(10);

                } else {
                    socketOut.writeInt(4);
                    System.out.println("sent no such file error");
                }
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

        if (type == -1 || type == 5 ) {
            return "";
        } else if (type == 1 || type == 6 || type == 7) {
            return processMessage1(socketIn);
        } else if (type == 2) {
            return processMessage2(socketIn);
        } else {
            throw new IllegalArgumentException("type " + type);
        }
    }

    static byte[] readFile(DataInputStream socketIn, int type) throws Exception {
        int length = socketIn.readInt();
        return socketIn.readNBytes(length);
    }

    static String processMessage1(DataInputStream value) throws Exception {
        String commandArgument = value.readUTF();
        return commandArgument;
    }

    static String processMessage2(DataInputStream value) throws Exception {
        String commandArgument = value.readUTF();
        return commandArgument;
    }

    static int checkFile(String commandArgument) {
        Path path = Paths.get(commandArgument);

        if (path.isAbsolute()) {
            return 3;
        } else if (Files.isRegularFile(path) && Files.exists(path)) {
            return 2;
        } else {
            return 4;
        }
    }

    static byte[] processFile(String commandArgument) throws Exception {
        Path path = Paths.get(commandArgument);

        return Files.readAllBytes(path);
    }

    static boolean isEndRequest(int type) {
        return type == -1;
    }

    static boolean isRegularMessage(int type) {
        return type == 1;
    }

    static boolean isUpdateRequest(int type) {
        return type == 5;
    }

    static boolean isUserMapping(int type) {
        return type == 6;
    }

    static boolean isAuthorSignature(int type) {
        return type == 7;
    }


}
