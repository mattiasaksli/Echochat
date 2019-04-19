import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Server {

    public static void main(String[] args) throws Exception {

        int port = 1337;

        List<Chatroom> chatrooms = new ArrayList<>();
        String chatroomGiantMessage = "";

        File f = new File("C:\\Users\\Ingvar\\Desktop\\ECHOBOYS\\OOP_Messenger_Project\\chatrooms");
        ArrayList<File> files = new ArrayList<>(Arrays.asList(f.listFiles()));

        for (File file : files) {
            List<String> chatroomContents = Files.readAllLines(file.toPath());
            String chatroomName = chatroomContents.get(0);
            for (int i = 1; i < chatroomContents.size(); i++) {
                chatroomGiantMessage = chatroomGiantMessage + (chatroomContents.get(i)) + "\n";
            }
            Chatroom chatroom = new Chatroom(chatroomName);
            chatrooms.add(chatroom);
        }

        HashMap<String, Chatroom> userAndChatroom = new HashMap<>();

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

        try (ServerSocket ss = ctx.getServerSocketFactory().createServerSocket(port)) {

            while (true) {

                System.out.println("now listening on :" + port);

                Socket socket = ss.accept();
                Thread t1 = new Thread(new ThreadSocket(socket, userAndChatroom, chatrooms, chatroomGiantMessage));
                t1.start();

                System.out.println("finished");
                System.out.println();
            }
        }
    }
}
