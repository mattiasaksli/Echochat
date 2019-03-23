import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
            while (true) {

                System.out.println("client connected; waiting for a message");
                int type = Commands.getType(dataIn);
                if (type == 0 ) {
                    System.out.println("Client ended connection");
                    break;

                }

                String clientMessage = Commands.readMessage(dataIn, type);

                System.out.println("received " + clientMessage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
