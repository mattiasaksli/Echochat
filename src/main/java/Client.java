import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {

        whatWouldYouLikeToDo(new ClientOptions(), new Scanner(System.in));
    }

    private static void connectToServer(String host, ClientOptions clientOptions, Scanner sc) throws Exception {

        int port = 1337;

        System.out.println("connecting to the awesome server");

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

        try (Socket socket = ctx.getSocketFactory().createSocket(host, port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream();
             DataOutputStream dataOut = new DataOutputStream(out);
             DataInputStream dataIn = new DataInputStream(in)) {

            String username = clientOptions.getUsername();
            Commands.writeUserToMap(dataOut, username);

            Thread update = new Thread(new Update(dataOut, dataIn, username));
            update.start();

            int type = MessageTypes.TEXT.getValue();

            System.out.println(username + " connected; sending data");

            if (type == MessageTypes.TEXT.getValue()) {

                while (sc.hasNext()) {

                    String toSend = username + ": " + sc.nextLine();

                    if ((toSend.equals(username + ": END"))) {
                        Commands.writeEnd(dataOut);
                        break;
                    }

                    Commands.messageAuthor(dataOut, username);
                    Commands.writeMessage(dataOut, toSend, type, true);
                }
            }
            //TODO implement file transferring
        }
        System.out.println("finished");
    }

    private static void whatWouldYouLikeToDo(ClientOptions clientOptions, Scanner sc) throws Exception {

        int chosenOption;
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
                        optionConnectToLocal(clientOptions, sc);
                        break;
                    case 4:
                        optionConnectToEC2(clientOptions, sc);
                        break;
                    case 5:
                        optionExit(clientOptions);
                        break;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input!\n");
            whatWouldYouLikeToDo(clientOptions, sc);
        }
    }

    private static void optionConnectToEC2(ClientOptions clientOptions, Scanner sc) throws Exception {
        if (clientOptions.loggedIn())
            connectToServer("3.17.78.222", clientOptions, sc);
        else {
            System.out.println("You must be logged in first!");
            whatWouldYouLikeToDo(clientOptions, sc);
        }
    }

    private static void optionConnectToLocal(ClientOptions clientOptions, Scanner sc) throws Exception {
        if (clientOptions.loggedIn())
            connectToServer("localhost", clientOptions, sc);
        else {
            System.out.println("You must be logged in first!");
            whatWouldYouLikeToDo(clientOptions, sc);
        }

    }

    private static void optionExit(ClientOptions clientOptions) {

        clientOptions.exit();
    }

    private static void optionCreateNewAccount(ClientOptions clientOptions, Scanner sc) throws Exception {
        if (clientOptions.loggedIn()) {
            System.out.println("You have already created an account!");
            whatWouldYouLikeToDo(clientOptions, sc);
        }
        clientOptions.createNewAccount(sc);
        clientOptions.login(sc);
        whatWouldYouLikeToDo(clientOptions, sc);
    }

    private static void optionLogin(ClientOptions clientOptions, Scanner sc) throws Exception {
        if (clientOptions.loggedIn()) {
            System.out.println("You are already logged in!");
            whatWouldYouLikeToDo(clientOptions, sc);
        } else {
            clientOptions.login(sc);
            whatWouldYouLikeToDo(clientOptions, sc);
        }
    }

}