import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static SSLContext getSSLContext() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        ClassLoader cl = Server.class.getClassLoader();
        String storePass = "secret";
        KeyStore store;
        try (InputStream keyIn = cl.getResourceAsStream("keystore.p12")) {
            store = KeyStore.getInstance("PKCS12");
            store.load(keyIn, storePass.toCharArray());
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(store, storePass.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(keyManagers, null, null);

        return ctx;
    }

    public static void main(String[] args) throws Exception {

        int port = 1337;

        List<Chatroom> chatrooms = new ArrayList<>();

        if (!Files.exists(Path.of("chatrooms"))) {
            new File("chatrooms").mkdir();
        }

        SSLContext ctx = getSSLContext();

        try (ServerSocket ss = ctx.getServerSocketFactory().createServerSocket(port)) {

            while (true) {

                System.out.println("now listening on :" + port);

                Socket socket = ss.accept();

                Thread t1 = new Thread(new ThreadSocket(chatrooms, socket));
                t1.start();

                System.out.println(t1 + " created");
                System.out.println();
            }
        }
    }
}