import java.io.*;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ThreadSocket implements Runnable {

    final Socket socket;
    ArrayBlockingQueue<String> messages;

    public ThreadSocket(Socket ss, ArrayBlockingQueue<String> messages) {
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

                if (type == -1) {
                    System.out.println("ending connection");
                    break;
                }

                String clientMessage = Commands.readMessage(dataIn, type);
                System.out.println("message got through: " + messages.offer(clientMessage));

                if (type == 1) {
                    System.out.println("received " + clientMessage);
                }

                if (type == 5) {

                    String message = "";
                    while (messages.peek() != null) {
                        String savedmessage = messages.take();
                        message = message.concat(savedmessage + "\n");
                        System.out.print(message);
                    }

                    Commands.writeMessage(dataOut, message, 1, false);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
