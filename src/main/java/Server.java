import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws Exception {

        int port = 1337;
        try (ServerSocket ss = new ServerSocket(port)) {

            while (true) {

                System.out.println("now listening on :" + port);

                Socket socket = ss.accept();
                Socket socket2 = ss.accept();
                Thread t1 = new Thread(new ThreadSocket(socket, socket2));
                t1.start();


                System.out.println("finished");
                System.out.println();

            }

        }

    }
}
