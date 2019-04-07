import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ThreadSocket implements Runnable {

    private String username;
    private final Socket socket;
    private HashMap<String, String> messages;
    private Commands commands = new Commands();

    ThreadSocket(Socket ss, HashMap<String, String> messages) {
        this.socket = ss;
        this.messages = messages;
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

                int type = commands.getType(dataIn);

                if (type == MessageTypes.REGISTRATION_REQ.value()) {
                    int response = commands.registerUser(dataIn);
                    dataOut.writeInt(response);
                    break;
                }

                if (type == MessageTypes.LOGIN_REQ.value()) {
                    int response = commands.loginUser(dataIn);
                    dataOut.writeInt(response);
                    break;
                }

                if (type == MessageTypes.AUTHOR_SIGNATURE.value()) {
                    username = commands.getUsername(dataIn);
                    type = commands.getType(dataIn);
                }

                String clientMessage = commands.readMessage(dataIn, type);

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

                    commands.writeMessage(dataOut, message, MessageTypes.TEXT.value(), false);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
