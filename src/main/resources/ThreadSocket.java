import java.io.*;
import java.net.Socket;

public class ThreadSocket implements Runnable {

    final Socket socket;

    public ThreadSocket(Socket ss) {
        this.socket = ss;
    }

    @Override
    public void run() {
        try (socket;
             InputStream in = socket.getInputStream();
             DataInputStream dataIn = new DataInputStream(in);
             OutputStream out = socket.getOutputStream();
             DataOutputStream dataOut = new DataOutputStream(out)) {

            System.out.println("client connected; waiting for a message");
            int type = Commands.getType(dataIn);

            // Thread.sleep(5000); //used this for testing

            String clientMessage = Commands.readMessage(dataIn, type);

            if (type == 1) {
                System.out.println("received " + clientMessage);
                System.out.println("echoing message '" + clientMessage + "' back");

            } else {
                System.out.println("received file request, preparing to send");
            }


            Commands.writeMessage(dataOut, clientMessage, type, false);


        } catch (Exception e) {
            throw new RuntimeException();
        }


    }
}
