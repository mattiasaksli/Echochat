import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Update implements Runnable {

    private DataOutputStream dataOut;
    private DataInputStream dataIn;
    private ClientOptions clientOptions;

    Update(DataOutputStream dataOut, DataInputStream dataIn, ClientOptions clientOptions) {
        this.dataOut = dataOut;
        this.dataIn = dataIn;
        this.clientOptions = clientOptions;
    }

    @Override
    public void run() {

        while (true) {
            try {
                // TEXTS

                Thread.sleep(500);
                Commands.writeUpdateRequest(dataOut);

                int length = dataIn.readInt();

                for (int i = 0; i < length; i++) {
                    int gotType = Commands.getType(dataIn);
                    String message = Commands.readMessage(dataIn, gotType);

                    gotType = Commands.getType(dataIn);
                    long timestamp = Long.parseLong(Commands.readMessage(dataIn, gotType).trim());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date resultDate = new Date(timestamp);

                    gotType = Commands.getType(dataIn);
                    String author = Commands.readMessage(dataIn, gotType).trim();

                    //Message detailedMessage = new Message(timestamp, author, message); // might be of use at some point

                    message = "[" + sdf.format(resultDate) + "] " + author + " >>> " + message;

                    message = message.trim();
                    if (!message.isEmpty() && !clientOptions.getMutedList().contains(author)) {
                        System.out.print(message + "\n");
                    }
                }

                // FILES

                Commands.writeFileUpdateRequest(dataOut);

                int fileLength = dataIn.readInt();

                for (int i = 0; i < fileLength; i++) {
                    int gotType = Commands.getType(dataIn);
                    byte[] file = Commands.readFile(dataIn);
                    String fileName = dataIn.readUTF();

                    if (!Files.exists(Path.of("received_files"))) {
                        new File("received_files").mkdir();
                    }

                    Files.write(Paths.get("received_files\\" + fileName), file);
                    System.out.print("You received a file " + fileName + "\n");

                }
            } catch (SocketException e) {
                System.out.println("Connection terminated"); // TODO fix this.
                break;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }

}