package wordReview;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.stream.Collectors.*;

public class Parser {

    private File knowl;
    private File input;
    public Set<String> knowledge = new HashSet<>();
    public Map<String, Integer> inputWords = new LinkedHashMap<>();

    public Parser(File input, File knowl) {
        this.input = input;
        this.knowl = knowl;
    }

    public boolean isPartOfWord(char p) {
        return Character.isLetter(p) || p == '\'';
    }

    public void parse(BufferedReader reader, boolean isInput) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            int i = 0;
            while (i < line.length()) {
                if (isPartOfWord(line.charAt(i))) {
                    int start = i;
                    while ((i < line.length()) && isPartOfWord(line.charAt(i))) {
                        i++;
                    }
                    String word = line.substring(start, i).toLowerCase();
                    if (isInput) {
                        if (!knowledge.contains(word)) {
                            Integer emp = inputWords.getOrDefault(word, 0) + 1;
                            inputWords.put(word, emp);
                        }
                    } else {
                        knowledge.add(word);
                    }
                }
                i++;
            }
        }
    }

    public Map<String, Integer> compare() {
        Map<String, Integer> unknown;

        try (BufferedReader knowledgeReader = new BufferedReader(new InputStreamReader(new FileInputStream(knowl), StandardCharsets.UTF_8))) {
            try (BufferedReader subtitleReader = new BufferedReader(new InputStreamReader(new FileInputStream(input), StandardCharsets.UTF_8))) {
                parse(knowledgeReader, false);               /// parse knowledge
                parse(subtitleReader, true);                 /// parse subtitles

                unknown = inputWords                                /// sort unknown inputWords by numbers
                        .entrySet()
                        .stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

                return unknown;

            } catch (IOException e) {
                System.out.println("Parser fault exception");
            }
        } catch (IOException e) {
            System.out.println("Parser couldn't open subtitle_reader");
        }
        throw new RuntimeException(">>> Parser fault <<<");
    }
}