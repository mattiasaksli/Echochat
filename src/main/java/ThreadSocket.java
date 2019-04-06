import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ThreadSocket implements Runnable {

    String username;
    String clientMessage;
    final Socket socket;
    HashMap<String, String> messages;

    public ThreadSocket(Socket ss, HashMap<String, String> messages) {
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

                if (Commands.isAuthorSignature(type)) {
                    username = Commands.getUsername(dataIn);
                    type = Commands.getType(dataIn);
                }

                clientMessage = Commands.readMessage(dataIn, type);

                if (Commands.isEndRequest(type)) {
                    System.out.println("ending connection");
                    break;
                }

                if (Commands.isUserMapping(type)) {
                    messages.put(clientMessage, "");
                    System.out.println(messages);
                }

                if (Commands.isRegularMessage(type)) {

                    for (String key : messages.keySet()) {
                        if (username.equals(key)) {
                        } else {
                            messages.replace(key, clientMessage);
                        }
                    }

                    System.out.println("received " + clientMessage);
                }

                if (Commands.isUpdateRequest(type)) {

                    String message = "";

                    if (!"".equals(messages.get(username))) {
                        message = messages.get(username);
                        messages.replace(username, "");
                    }

                    System.out.print(message);

                    Commands.writeMessage(dataOut, message, 1, false);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
