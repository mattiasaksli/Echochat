import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

class Commands {

    private final Argon2 argon2 = Argon2Factory.create();

    void writeEnd(DataOutputStream socketOut) throws Exception {
        socketOut.writeInt(MessageTypes.END_SESSION.value());
    }

    void writeUpdateRequest(DataOutputStream socketOut) throws Exception {
        socketOut.writeInt(MessageTypes.UPDATE_REQ.value());
    }

    void writeUserToMap(DataOutputStream socketOut, String username) throws Exception {
        socketOut.writeInt(MessageTypes.USER_MAP.value());
        socketOut.writeUTF(username);
    }

    void messageAuthor(DataOutputStream socketOut, String username) throws Exception {
        socketOut.writeInt(MessageTypes.AUTHOR_SIGNATURE.value());
        socketOut.writeUTF(username);
    }

    void writeMessage(DataOutputStream socketOut, String message, int type, boolean isRequest) throws Exception {
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

    int getType(DataInputStream socketIn) throws Exception {
        return socketIn.readInt();
    }

    String getUsername(DataInputStream socketIn) throws Exception {
        return socketIn.readUTF();
    }

    String readMessage(DataInputStream socketIn, int type) throws Exception {

        if (type == MessageTypes.END_SESSION.value() ||
                type == MessageTypes.UPDATE_REQ.value()) {
            return "";
        } else if (type == MessageTypes.TEXT.value() ||
                type == MessageTypes.USER_MAP.value() ||
                type == MessageTypes.AUTHOR_SIGNATURE.value()) {
            return processMessage1(socketIn);
        } else {
            throw new IllegalArgumentException("type " + type);
        }
    }

    private String processMessage1(DataInputStream value) throws Exception {
        return value.readUTF();
    }

    int registerUser(DataInputStream socketIn) throws IOException {
        String userName = socketIn.readUTF();
        String passWord = socketIn.readUTF();

        String hash = argon2.hash(30, 65536, 1, passWord.toCharArray());

        if (!Files.exists(Path.of("credentials.txt"))) {
            Files.createFile(Path.of("credentials.txt"));
        }

        List<String> credentials = Files.readAllLines(Path.of("credentials.txt"));

        for (String credential : credentials) {
            String[] split = credential.split("\t");
            if (userName.equals(split[0])) {
                return MessageTypes.REGISTRATION_WRONG_USERNAME.value();
            }
            if (passWord.equals(split[1])) {
                return MessageTypes.REGISTRATION_WRONG_PASSWORD.value();
            }
        }

        Path path = Path.of("credentials.txt");
        String newUser = userName + "\t" + hash;
        Files.write(path, Collections.singletonList(newUser), StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);
        return MessageTypes.REGISTRATION_SUCCESS.value();
    }

    int loginUser(DataInputStream socketIn) throws IOException {
        String userName = socketIn.readUTF();
        String passWord = socketIn.readUTF();

        if (!Files.exists(Path.of("credentials.txt"))) {
            return MessageTypes.LOGIN_MISSING_DB.value();
        }

        List<String> credentials = Files.readAllLines(Path.of("credentials.txt"));

        for (String credential : credentials) {
            String[] split = credential.split("\t");
            if (userName.equals(split[0])) {
                if (argon2.verify(split[1], passWord)) {
                    return MessageTypes.LOGIN_SUCCESS.value();
                } else {
                    return MessageTypes.LOGIN_WRONG_PASSWORD.value();
                }
            }
        }
        return MessageTypes.LOGIN_WRONG_USERNAME.value();
    }
}
