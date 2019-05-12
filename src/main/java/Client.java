import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Scanner;

public class Client {

    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    private Client(DataInputStream dataIn, DataOutputStream dataOut) {
        this.dataIn = dataIn;
        this.dataOut = dataOut;
    }

    public static void main(String[] args) throws Exception {

        int port = 1337;
        String IP = args[0];

        Scanner sc = new Scanner(System.in);

        System.out.println("\nConnecting to server...");

        SSLContext ctx = getSSLContext();

        try (Socket socket = ctx.getSocketFactory().createSocket(IP, port);
             DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataIn = new DataInputStream(socket.getInputStream())) {

            System.out.println("\nConnected!\n");

            Client client = new Client(dataIn, dataOut);

            client.whatWouldYouLikeToDo(new ClientOptions(), sc);

        } catch (SocketException s) {
            System.out.println("\nWrong IP address!\n");
        }

    }

    private static SSLContext getSSLContext() throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, KeyManagementException {
        ClassLoader cl = Client.class.getClassLoader();
        String storePass = "secret";
        KeyStore store;
        try (InputStream keyIn = cl.getResourceAsStream("truststore.p12")) {
            store = KeyStore.getInstance("PKCS12");
            store.load(keyIn, storePass.toCharArray());
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(store);
        TrustManager[] trustManagers = tmf.getTrustManagers();

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, trustManagers, null);
        return ctx;
    }

    private void whatWouldYouLikeToDo(ClientOptions clientOptions, Scanner sc) throws Exception {

        int chosenOption = -1;
        while (chosenOption != 0) {

            clientOptions.welcome();

            try {
                if (sc.hasNext()) {
                    chosenOption = Integer.parseInt(sc.next());
                    switch (chosenOption) {
                        case 1:
                            optionLogin(clientOptions, sc);
                            break;
                        case 2:
                            optionCreateNewAccount(clientOptions, sc);
                            break;
                        case 3:
                            optionChatroom(clientOptions, sc);
                            break;
                        case 0:
                            optionExit();
                            break;
                        default:
                            System.out.println("\nPlease choose a valid option!\n");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("\nPlease enter a number!\n");
            }
        }
    }

    private void optionChatroom(ClientOptions clientOptions, Scanner sc) throws Exception {
        if (!clientOptions.loggedIn()) {
            System.out.println("\nYou need to log in first!\n");
        } else {
            clientOptions.connectToChatroom(clientOptions, sc, dataIn, dataOut);
        }
    }

    private void optionCreateNewAccount(ClientOptions clientOptions, Scanner sc) throws Exception {
        if (clientOptions.loggedIn()) {
            System.out.println("\nYou have already created an account!\n");
        } else {
            clientOptions.createNewAccount(sc, dataIn, dataOut);
            if (clientOptions.isAccountCreated()) {
                System.out.println("##############################\n");
                clientOptions.login(sc, dataIn, dataOut);
            }
        }
    }

    private void optionLogin(ClientOptions clientOptions, Scanner sc) throws Exception {
        if (clientOptions.loggedIn()) {
            System.out.println("\nYou are already logged in!\n");
        } else {
            clientOptions.login(sc, dataIn, dataOut);
        }
    }

    private void optionExit() throws Exception {
        System.out.println("Bye-bye!");
        dataOut.writeInt(MessageTypes.END_SESSION.value());
        System.exit(0);
    }

}