import com.google.common.base.Strings;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurseFilter {


    private static List<String> getCurseList() throws IOException {
        URL url = new URL("http://www.bannedwordlist.com/lists/swearWords.txt");
        byte[] bytes = url.openStream().readAllBytes();
        String[] lines = new String(bytes, StandardCharsets.UTF_8).trim().split("\\r\\n");
        return new ArrayList<>(Arrays.asList(lines));
    }

    private static List<String> getLocalCurseList() throws IOException {
        if (Files.notExists(Path.of("swearwords.txt")))
            Files.createFile(Path.of("swearwords.txt"));
        return Files.readAllLines(Path.of("swearwords.txt"));
    }

    public static String replaceCurseWordsWithAsterisks(String message) throws IOException {
        StringBuilder outgoingMessage = new StringBuilder();
        List<String> curses = getCurseList();
        List<String> cursesLocal = getLocalCurseList();
        for (String string : message.split(" ")) {
            if (cursesLocal.contains(string.toLowerCase()) || curses.contains(string.toLowerCase())) {
                String sone = Strings.repeat("*", string.length());
                outgoingMessage.append(" ").append(sone);

            } else
                outgoingMessage.append(" ").append(string);
        }
        return outgoingMessage.toString();
    }


    public static void addCurseWords(String word) throws IOException {
        if (Files.notExists(Path.of("swearwords.txt")))
            Files.createFile(Path.of("swearwords.txt"));
        List<String> curseList = Files.readAllLines(Path.of("swearwords.txt"));
        curseList.add(word);
        Files.write(Path.of("swearwords.txt"), curseList);
        System.out.println("Curse word added to blocked list!");
    }

    public static void removeCurseWords(String word) throws IOException {
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
