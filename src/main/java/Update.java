import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.SocketException;
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

        while (!Thread.interrupted()) {
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
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date resultDate = new Date(timestamp);

                    gotType = Commands.getType(dataIn);
                    String author = Commands.readMessage(dataIn, gotType).trim();

                    String messageString = "[" + sdf.format(resultDate) + "] " + author + " >>> " + message;

                    messageString = messageString.trim();
                    if (!messageString.isEmpty() && !clientOptions.getMutedList().contains(author)) {
                        if (clientOptions.getTtsState()) {
                            TextSpeech.sayMessage(message);
                        }
                        System.out.print(messageString + "\n");
                    }
                }

            } catch (SocketException e) {
                System.out.println("Connection terminated"); // TODO fix this.
                break;
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

}