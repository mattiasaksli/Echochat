import com.google.common.base.Strings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CurseFilter {


    public static List<String> getCurseList() throws IOException {
        return Files.readAllLines(Path.of("swearwords.txt"));
    }

    public static String replaceCurseWordsWithAsterisks(String message) throws IOException {
        StringBuilder outgoingMessage = new StringBuilder();

        for (String string : message.split(" ")) {
            if (getCurseList().contains(string.toLowerCase())) {
                String sone = Strings.repeat("*", string.length());
                outgoingMessage.append(" ").append(sone);

            } else
                outgoingMessage.append(" ").append(string);
        }
        return outgoingMessage.toString();
    }
}
