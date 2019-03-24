import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {

        ClientOptions.welcome();
        Scanner sc1 = new Scanner(System.in);

        while (true) {
            int chooseOption = Integer.parseInt(sc1.next());

            // LOG IN
            if (chooseOption == 1) {
                ClientOptions.login();
                if (ClientOptions.loggedIn())
                    connectToServer();
                else
                    ClientOptions.welcome();

            //CREATE NEW ACCOUNT
            } else if (chooseOption == 2) {
                ClientOptions.createNewAccount();
                ClientOptions.login();
                if (ClientOptions.loggedIn())
                    connectToServer();

            // EXIT THE PROGRAM
            } else if (chooseOption == 5) {
                ClientOptions.exit();
                break;
            }
        }
    }

    private static void connectToServer() throws Exception {
        System.out.println("connecting to server");
        try (Socket socket = new Socket("localhost", 1337);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream();
             DataOutputStream dataOut = new DataOutputStream(out);
             DataInputStream dataIn = new DataInputStream(in)) {

            //Path saveToPath;
            //int type = Integer.parseInt(args[0]);
            int type = 1;

            System.out.println("connected; sending data");

            Scanner sc = new Scanner(System.in);

            if (type == 1) {

                while (sc.hasNextLine()) {

                    String toSend = sc.nextLine();

                    if ((toSend.equals("END"))) {
                        Commands.writeMessage(dataOut, "", -1, true);
                        int gotType = Commands.getType(dataIn);
                        Commands.readMessage(dataIn, gotType);
                        break;
                    }

                    Commands.writeMessage(dataOut, toSend, 1, true);
                    System.out.println("sent " + toSend);

                    Commands.getType(dataIn);
                    String clientMessageEcho = Commands.readMessage(dataIn, type);
                    System.out.println("received " + clientMessageEcho);

                }

            }
            /*else if (type == 2) {
                String path = args[1];
                saveToPath = Paths.get(args[2]);

                Commands.writeMessage(dataOut, path, 2, true);

                int checkType = Commands.getType(dataIn);

                if (checkType == 3) {
                    throw new IllegalArgumentException("error, server-relative path needed");

                } else if (checkType == 2) {
                    byte[] file = Commands.readFile(dataIn, checkType);
                    Files.write(saveToPath, file);
                    System.out.println("received" + saveToPath.toString());
                    System.out.println("File size: \n" + new File(args[2]).length() + " bytes\n");
                    System.out.println("File was written to: " + saveToPath);

                } else {
                    throw new IllegalArgumentException("error, could not find file");
                }



            }*/

        }

        System.out.println("finished");
        System.out.println();

    }

}