import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.concurrent.ArrayBlockingQueue;

public class Server {

    public static void main(String[] args) throws Exception {

        int port = 1337;

        ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<>(100);

        File storeFile = new File("keystore.p12");
        String storePass = "secret";

        KeyStore store = KeyStore.getInstance(storeFile, storePass.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(store, storePass.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(keyManagers, null, null);

        try (ServerSocket ss = ctx.getServerSocketFactory().createServerSocket(port)) {

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
