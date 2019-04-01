import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Server {

    public static void main(String[] args) throws Exception {

        ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<>(100);

        int port = 1337;
        try (ServerSocket ss = new ServerSocket(port)) {

            while (true) {

                System.out.println("now listening on :" + port);

                Socket socket = ss.accept();
                Thread t1 = new Thread(new ThreadSocket(socket, messages));
                t1.start();


                System.out.println("finished");
                System.out.println();

            }

        }

    }
}
