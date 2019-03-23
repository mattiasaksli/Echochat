import java.io.*;
import java.net.Socket;

public class ThreadSocket implements Runnable {

    final Socket socket;
    final Socket socket2;

    public ThreadSocket(Socket ss,Socket ss2) {
        this.socket = ss;
        this.socket2 = ss2;
    }

    @Override
    public void run() {
        try (socket;
             InputStream in = socket.getInputStream();
             DataInputStream dataIn = new DataInputStream(in);
             OutputStream out = socket.getOutputStream();
             DataOutputStream dataOut = new DataOutputStream(out);
             socket2;
             InputStream in2 = socket2.getInputStream();
             DataInputStream dataIn2 = new DataInputStream(in2);
             OutputStream out2 = socket2.getOutputStream();
             DataOutputStream dataOut2 = new DataOutputStream(out2)) {

            while (true) {

                System.out.println("client connected; waiting for a message");
                int type = Commands.getType(dataIn);
                int type2 = Commands.getType(dataIn2);

                String clientMessage = Commands.readMessage(dataIn, type);
                String clientMessage2 = Commands.readMessage(dataIn2, type);

                if (type == 1 || type == 2) {
                    System.out.println("received " + clientMessage);
                    System.out.println("echoing message '" + clientMessage + "' back");
                    System.out.println("received " + clientMessage2);
                    System.out.println("echoing message '" + clientMessage2 + "' back");

                } else {
                    System.out.println("received file request, preparing to send");
                }


                Commands.writeMessage(dataOut, clientMessage2, type, false);
                Commands.writeMessage(dataOut2, clientMessage, type2, false);
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }


    }
}
