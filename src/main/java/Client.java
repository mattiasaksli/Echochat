import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {

        int port = 1337;

        Scanner sc = new Scanner(System.in);
        String IP = null;

        while (IP == null) {

            System.out.println("Enter server IP:");
            IP = sc.next();

            System.out.println("\nConnecting to server...");

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

            try (Socket socket = ctx.getSocketFactory().createSocket(IP, port);
                 OutputStream out = socket.getOutputStream();
                 InputStream in = socket.getInputStream();
                 DataOutputStream dataOut = new DataOutputStream(out);
                 DataInputStream dataIn = new DataInputStream(in)) {

                System.out.println("\nConnected!\n");

                Client client = new Client();

                client.whatWouldYouLikeToDo(new ClientOptions(), sc, dataIn, dataOut);

            } catch (SocketException s) {
                System.out.println("\nWrong IP address!\n");
                IP = null;
            }
        }
    }

    private void whatWouldYouLikeToDo(ClientOptions clientOptions, Scanner sc,
                                      DataInputStream dataIn, DataOutputStream dataOut) throws Exception {

        int chosenOption = -1;
        while (chosenOption != 0) {

            clientOptions.welcome();

            try {
                if (sc.hasNext()) {
                    chosenOption = Integer.parseInt(sc.next());
                    switch (chosenOption) {
                        case 1:
                            optionLogin(clientOptions, sc, dataIn, dataOut);
                            break;
                        case 2:
                            optionCreateNewAccount(clientOptions, sc, dataIn, dataOut);
                            break;
                        case 3:
                            optionChatroom(clientOptions, sc, dataIn, dataOut);
                            break;
                        case 0:
                            optionExit(dataOut);
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

    private void optionChatroom(ClientOptions clientOptions, Scanner sc,
                                DataInputStream dataIn, DataOutputStream dataOut) throws Exception {
        if (!clientOptions.loggedIn()) {
            System.out.println("\nYou need to log in first!\n");
        } else {
            clientOptions.connectToChatroom(clientOptions, sc, dataIn, dataOut);
        }
    }

    private void optionCreateNewAccount(ClientOptions clientOptions, Scanner sc,
                                        DataInputStream dataIn, DataOutputStream dataOut) throws Exception {
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

    private void optionLogin(ClientOptions clientOptions, Scanner sc,
                             DataInputStream dataIn, DataOutputStream dataOut) throws Exception {
        if (clientOptions.loggedIn()) {
            System.out.println("\nYou are already logged in!\n");
        } else {
            clientOptions.login(sc, dataIn, dataOut);
        }
    }

    private void optionExit(DataOutputStream dataOut) throws Exception {
        System.out.println("Bye-bye!");
        dataOut.writeInt(MessageTypes.END_SESSION.value());
        System.exit(0);
    }

}