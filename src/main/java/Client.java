import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {

        clientOptions clientOptions = new clientOptions();
        clientOptions.welcome();

        while (sc.hasNextLine()) { //set from true to hasNextLine during merge conflict
            int chooseOption = Integer.parseInt(sc.next());

            if (chooseOption == 1) {            // LOG IN
                clientOptions.login();
                if (clientOptions.loggedIn())
                    connectToServer();
                else
                    clientOptions.welcome();

            } else if (chooseOption == 2) {     //CREATE NEW ACCOUNT
                clientOptions.createNewAccount();
                clientOptions.login();
                if (clientOptions.loggedIn())
                    connectToServer();

            } else if (chooseOption == 3) {
                //TODO Add options
                break;

            } else if (chooseOption == 4) {
                //TODO Add options
                break;

            } else if (chooseOption == 5) {     // EXIT THE PROGRAM
                clientOptions.exit();
                break;

            } else if (chooseOption == 6) {
                //TODO Add options
                break;

            } else if (chooseOption == 7) {
                //TODO Add options
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

            Thread update = new Thread(new Update(dataOut, dataIn));
            update.start();

            //Path saveToPath;
            //int type = Integer.parseInt(args[0]);
            int type = 1;

            System.out.println("connected; sending data");


            if (type == 1) {

                while (sc.hasNextLine()) { //merge-conflict: was true

                    String toSend = sc.nextLine();

                    if ((toSend.equals("END"))) {
                        Commands.writeEnd(dataOut);
                        break;

                    } /*else if ((toSend.equals("update"))) {
                        Commands.writeUpdateRequest(dataOut);
                        int gotType = Commands.getType(dataIn);
                        String message = Commands.readMessage(dataIn, gotType);
                        System.out.print(message);
                    }*/

                    Commands.writeMessage(dataOut, toSend, type, true);

                    //Commands.getType(dataIn);
                    //String clientMessageEcho = Commands.readMessage(dataIn, type);
                    //System.out.println("received " + message);

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
            sc.close();
        }

        System.out.println("finished");
        System.out.println();

    }

}