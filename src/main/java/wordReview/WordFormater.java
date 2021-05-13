package wordReview;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordFormater {
    private File file;

    public WordFormater(File got) {
        this.file = got;
    }

    public static boolean isPartOfWord(char p) {
        return Character.isLetter(p) || p == '\'';
    }

    public void rewrite() {
        NavigableSet<String> words = new TreeSet<>();
        try {
            try (BufferedReader knowledge = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String know;
                while ((know = knowledge.readLine()) != null) {
                    int i = 0;
                    while (i < know.length()) {
                        if (isPartOfWord(know.charAt(i))) {
                            int start = i;
                            while ((i < know.length()) && isPartOfWord(know.charAt(i))) {
                                i++;
                            }
                            String word = know.substring(start, i).toLowerCase();
                            words.add(word);
                        }
                        i++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            System.out.println("UnsupportedEncodingException" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException" + e.getMessage());
            return;
        }

        try {
            try (BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                wr.write("There are " + words.size() + " words:");
                Iterator<String> iterator = words.iterator();
                char litera = ' ';
                int newline = 7;
                while (iterator.hasNext()) {
                    String cur = iterator.next();

                    if (cur.charAt(0) != litera) {
                        litera = cur.charAt(0);
                        wr.write("\n\n" + (litera + "").toUpperCase());
                        wr.newLine();
                        newline = 7;
                    }

                    wr.write(cur + ", ");
                    if (newline == 0) {
                        wr.newLine();
                        newline = 7;
                    } else {
                        newline--;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("IOException" + e.getMessage());
        }
    }
}