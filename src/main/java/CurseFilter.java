import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CurseFilter {

    List<String> cursesFromWeb;
    List<String> cursesLocal;


    List<String> getCurseList() throws IOException {
        URL url = new URL("http://www.bannedwordlist.com/lists/swearWords.txt");
        InputStream inputStream = url.openStream();
        byte[] bytes = inputStream.readAllBytes();
        inputStream.close();

        String[] lines = new String(bytes, StandardCharsets.UTF_8).trim().split("\\r\\n");
        return new ArrayList<>(Arrays.asList(lines));
    }

    List<String> getLocalCurseList() throws IOException {
        if (Files.notExists(Path.of("swearwords.txt")))
            Files.createFile(Path.of("swearwords.txt"));
        return Files.readAllLines(Path.of("swearwords.txt"));
    }

String replaceCurseWordsWithAsterisks(String message, List<String> cursesLocal, List<String> cursesFromWeb) throws IOException {
        StringBuilder outgoingMessage = new StringBuilder();
        for (String string : message.split(" ")) {
            if (cursesLocal.contains(string.toLowerCase()) || cursesFromWeb.contains(string.toLowerCase())) {
                String sone = Strings.repeat("*", string.length());
                outgoingMessage.append(" ").append(sone);

            } else
                outgoingMessage.append(" ").append(string);
        }
        return outgoingMessage.toString();
    }


    void addCurseWords(String word) throws IOException {
        if (Files.notExists(Path.of("swearwords.txt")))
            Files.createFile(Path.of("swearwords.txt"));
        List<String> curseList = Files.readAllLines(Path.of("swearwords.txt"));
        curseList.add(word);
        Files.write(Path.of("swearwords.txt"), curseList);
        System.out.println("Curse word added to blocked list!");
    }

    void removeCurseWords(String word) throws IOException {
        if (Files.notExists(Path.of("swearwords.txt")))
            return;
        List<String> curseList = Files.readAllLines(Path.of("swearwords.txt"));
        try {
            curseList.remove(word);
            Files.write(Path.of("swearwords.txt"), curseList);
            System.out.println("Curse word removed from blocked list!");
        } catch (Exception e) {
            System.out.println("No such word found!");

        }

    }
}
