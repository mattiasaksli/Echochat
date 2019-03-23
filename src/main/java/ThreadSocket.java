import java.io.*;
import java.net.Socket;

public class ThreadSocket implements Runnable {

    final Socket socket;
    final Socket socket2;

    public ThreadSocket(Socket ss, Socket ss2) {
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

                if (type == -1 || type2 == -1) {
                    System.out.println("ending connection");
                    Commands.writeMessage(dataOut, "", -1, false);
                    Commands.writeMessage(dataOut2, "", -1, false);
                    break;
                }

                String clientMessage = Commands.readMessage(dataIn, type);
                String clientMessage2 = Commands.readMessage(dataIn2, type);

                String combined = "\tClient1: '" + clientMessage + "'\n\t\tClient2: '" + clientMessage2 + "'";

                if (type == 1 || type2 == 1) {
                    System.out.println("received " + clientMessage);
                    System.out.println("received " + clientMessage2);

                    Commands.writeMessage(dataOut, combined, type, false);
                    Commands.writeMessage(dataOut2, combined, type2, false);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
