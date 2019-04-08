import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ThreadSocket implements Runnable {

    private String username;
    private final Socket socket;
    private HashMap<String, String> messages;
    private final Argon2 argon2 = Argon2Factory.create();

    ThreadSocket(Socket ss, HashMap<String, String> messages) {
        this.socket = ss;
        this.messages = messages;
    }

    private int registerUser(DataInputStream socketIn) throws IOException {
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

    private int loginUser(DataInputStream socketIn) throws IOException {
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

    @Override
    public void run() {
        try (socket;
             InputStream in = socket.getInputStream();
             DataInputStream dataIn = new DataInputStream(in);
             OutputStream out = socket.getOutputStream();
             DataOutputStream dataOut = new DataOutputStream(out)) {
            System.out.println("client connected");

            while (true) {

                int type = Commands.getType(dataIn);

                if (type == MessageTypes.REGISTRATION_REQ.value()) {
                    int response = registerUser(dataIn);
                    dataOut.writeInt(response);
                    break;
                }

                if (type == MessageTypes.LOGIN_REQ.value()) {
                    int response = loginUser(dataIn);
                    dataOut.writeInt(response);
                    break;
                }

                if (type == MessageTypes.AUTHOR_SIGNATURE.value()) {
                    username = Commands.getUsername(dataIn);
                    type = Commands.getType(dataIn);
                }

                String clientMessage = Commands.readMessage(dataIn, type);

                if (type == MessageTypes.END_SESSION.value()) {
                    System.out.println("ending connection");
                    break;
                }

                if (type == MessageTypes.USER_MAP.value()) {
                    messages.put(clientMessage, "");
                    System.out.println(messages);
                }

                if (type == MessageTypes.TEXT.value()) {

                    for (String key : messages.keySet()) {
                        if (username.equals(key)) {
                        } else {
                            messages.replace(key, clientMessage);
                        }
                    }

                    System.out.println("received " + clientMessage + "\n");
                }

                if (type == MessageTypes.UPDATE_REQ.value()) {

                    String message = "";

                    if (!"".equals(messages.get(username))) {
                        message = messages.get(username);
                        messages.replace(username, "");
                    }

                    System.out.print(message);

                    Commands.writeMessage(dataOut, message, MessageTypes.TEXT.value(), false);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
