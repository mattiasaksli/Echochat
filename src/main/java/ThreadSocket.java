import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ThreadSocket implements Runnable {

    private String username;
    private final Socket socket;
    private HashMap<String, String> messages;

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

                int type = Commands.getType(dataIn);

                if (type == MessageTypes.AUTHOR_SIGNATURE.getValue()) {
                    username = Commands.getUsername(dataIn);
                    type = Commands.getType(dataIn);
                }

                String clientMessage = Commands.readMessage(dataIn, type);

                if (type == MessageTypes.END_SESSION.getValue()) {
                    System.out.println("ending connection");
                    break;
                }

                if (type == MessageTypes.USER_MAP.getValue()) {
                    messages.put(clientMessage, "");
                    System.out.println(messages);
                }

                if (type == MessageTypes.TEXT.getValue()) {

                    for (String key : messages.keySet()) {
                        if (username.equals(key)) {
                        } else {
                            messages.replace(key, clientMessage);
                        }
                    }

                    System.out.println("received " + clientMessage + "\n");
                }

                if (type == MessageTypes.UPDATE_REQ.getValue()) {

                    String message = "";

                    if (!"".equals(messages.get(username))) {
                        message = messages.get(username);
                        messages.replace(username, "");
                    }

                    System.out.print(message);

                    Commands.writeMessage(dataOut, message, MessageTypes.TEXT.getValue(), false);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
