import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {
        System.out.println("connecting to server");
        try (Socket socket = new Socket("localhost", 1337);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream();
             DataOutputStream dataOut = new DataOutputStream(out);
             DataInputStream dataIn = new DataInputStream(in)) {

            int type = 1;

            System.out.println("connected; sending data");
            Scanner sc = new Scanner(System.in);

            while (sc.hasNextLine()) {
                String ok = sc.nextLine();
                if (!(ok.contains("END"))) {
                    String toSend = ok;

                    dataOut.writeInt(1);
                    dataOut.writeUTF(toSend);
                    System.out.println("sent " + toSend);

                } else {
                    dataOut.writeInt(0);

                    System.out.println("Ending connection");
                    break;
                }
            }
        }

        System.out.println("finished");
        System.out.println();
    }

}
