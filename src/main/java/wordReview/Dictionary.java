package wordReview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class Dictionary extends Thread {

    public static String splitByDefinitions(String response) {
        String[] definitions = response.split("\"definition\":");
        String[] examples = response.split("\"example\":");
        StringBuilder back = new StringBuilder();

        for (int i = 1; i < definitions.length && i < 3; i++) {
            String answer = definitions[i].split("\"")[1];
            String example = examples[i].split("\"")[1];

            example = example.replace("\\n", "");
            example = example.replace("\\r", "");
            example = example.replaceAll("[\\[\\]]", "");

            answer = answer.replace("\\n", "");
            answer = answer.replace("\\r", "");
            answer = answer.replace("\\", "");
            answer = answer.replaceAll("[\\[\\]]", "");

            back.append("    ").append(i).append(": ").append(answer).append("\n");
            back.append("        ").append(example).append("\n");
        }
        return String.valueOf(back);
    }

    public static Definition getDefinition(String word) {
        try {
            URL url = new URL("https://mashape-community-urban-dictionary.p.rapidapi.com/define?term=" + word);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("x-rapidapi-host", "mashape-community-urban-dictionary.p.rapidapi.com");
            connection.setRequestProperty("x-rapidapi-key", "ea2741a53amshb9e4b68d02ac997p17bf42jsn6a4eff9c8943");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();

            return new Definition(splitByDefinitions(response));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Definition(null);
    }
}
